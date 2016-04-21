(ns dribbble-scraper.core
  (:require [net.cgrand.enlive-html :as html]
            [clojure.string :as str])
  (:gen-class))

(def base-url "https://dribbble.com/")

(def email-regex #"['a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}")

(def services ["creative_market"
               "instagram"
               "facebook"
               "behance"
               "codepen"
               "twitter"
               "website"
               "github"
               "medium"])

(defn get-url [username]
  (str/join [base-url username]))

(defn fetch [url]
  (html/html-resource (java.net.URL. url)))

(defn get-usernames [url]
  (let [links (html/select (fetch url) [:.vcard :.url])]
    (map (fn [link]
           (let [href (get-in link [:attrs :href])]
             (subs href (+ (or (str/index-of href "/") -1) 1))))
         links)))


(defn get-inner-text [url-data selectors]
  (first (map html/text (html/select url-data selectors))))

(defn get-title [class details]
  (if-let [node (first (html/select details [class]))]
    (first (str/split (get-in node [:attrs :title]) #" "))))

(defn get-key-value [details string]
  (if-let [title (get-title (keyword (str/join [".elsewhere-" string])) details)]
    {(keyword string) title}))

(defn get-playbook [url-data username]
  (if (not-empty (html/select url-data [:.profile-details :.elsewhere-playbook]))
    {:playbook username}))

(defn not-team [url-data]
  (empty? (html/select url-data [:.pro-badge :.badge-link :.badge-team])))

(defn get-details [url-data]
  (let [details (html/select url-data [:.profile-details])]
    (reduce merge (map (partial get-key-value details) services))))

(defn get-email-addresses-from-bio [url-data]
  (if-let [bio (get-inner-text url-data [:.bio])]
    (if-let [email-addresses (re-seq email-regex bio)]
      {:email_addresses email-addresses})))

(defn get-skills [url-data]
  (if-let [skills (get-inner-text url-data [:.skills])]
    (str/split (str/trim skills) #",\s*")))

(defn get-data [url-data username]
  (if (not-team url-data)
    (merge
      {:location (get-inner-text url-data [:.profile-location :a])
       :username username
       :skills (get-skills url-data)
       :name (get-inner-text url-data [:.profile-name :.name]) ; XXX: Uncertain.
       :bio (get-inner-text url-data [:.bio])
       }
      (get-email-addresses-from-bio url-data) ; XXX: Uncertain. Takes any email from bio
      (get-playbook url-data username)
      (get-details url-data))))

(defn scrape-page [page]
  (filter (complement nil?)
          (map (fn [username]
                 (get-data (fetch (get-url username))
                           username))
               (get-usernames (get-url (format "designers?page=%s" page))))))

(defn scrape
  ([page results]
   (if-let [page-data (not-empty (scrape-page page))]
     (if (< page 2)
       (scrape (inc page) (concat results page-data))
       results)))
  ([]
   (scrape 1 '())))
