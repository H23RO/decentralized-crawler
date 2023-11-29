# DARS - Decentralized Article Retrieval System

## Description

DARS is a decentralized solution for article retrieval, it has three main components/applications (RetrievalCore, WebCrawler and WebScraper), it is written using the Spring Boot Java framework, and it is built using Maven.

## Components

The retrieval system is composed of a RetrievalCore, and multiple WebCrawler and WebScraper applications.

- **RetrievalCore** - manages the crawling and scraping process,
- **WebCrawler** - extracts the URLs and identifies the article containing web pages from a list of websites,
- **WebScraper** - extracts the article information from a list of URLs and sends it to the RetrievalCore.

## Deploying the whole system

1. Deploy the RetrievalCore component
   1. Create an empty database for the RetrievalCore.
   2. Configure the RetrievalCore component (follow the steps from the [RetrievalCore configuration](#retrievalcore-configuration) section).
   3. Start the RetrievalCore component (see section [Running each component](#running-each-component)). The database will be populated with the required tables and initialized based on the provided data in the directory with the site information and extraction template. Also, a crawler and a scraper user are created by default.
2. Deploy one or more WebCrawlers - for each WebCrawler perform the following steps:
   1. Create an empty database for the WebCrawler.
   2. Configure the WebCrawler component (follow the steps from the [WebCrawler configuration](#webcrawler-configuration) section). A valid set of user-password credentials must be provided in the configuration file (i.e., the user has to exist in the RetrievalCore database).
   3. Start the WebCrawler component (see section [Running each component](#running-each-component)). The database will be populated with the required tables, if required.
3. Deploy one or more WebScrapers - for each WebScraper perform the following steps:
   1. Create an empty database for the WebScraper (for the provided example, the database is an SQLite file, which is created when the application starts).
   2. Configure the WebScraper component (follow the steps from the [WebScraper configuration](#webscraper-configuration) section). A valid set of user-password credentials must be provided in the configuration file (i.e., the user has to exist in the RetrievalCore database).
   3. Start the WebScraper component (see section [Running each component](#running-each-component)). The database will be populated with the required tables, if required.
   
## Run the out-of-the-box example

The provided code contains an out-of-the-box example in which all three components run on the same computer and extract articles from a provided website (including the extraction template).

The following steps have to be taken:
1. Clone the repository
2. Have MySQL installed locally (the configuration files use the `root` username with no password).
3. Create a MySQL database for the RetrievalCore (i.e., `dars_retrievalcore`) and one for the WebCrawler (i.e., `dars_webcrawler`).
4. Run the RetrievalCore, WebCrawler and WebScraper by executing the `mvn spring-boot:run` command from each of the directories.


## RetrievalCore configuration

I. Update the general configuration file - `RetrievalCore/src/main/resources/application.yml` - and update at least the following:
1. `server.port` - port on which to start the server application (used so the crawlers and scraper can connect and obtain the site list, article URLs and extraction templates,
2. `spring.datasource.url` - database connection URL
3. `spring.datasource.username` - username for connecting to the database
4. `spring.datasource.password` - password for connecting to the database
5. `dars.config.site-info-path` - path to the directory with JSON containing the site information and extraction template
6. `dars.config.server-list-path` - path to the server list URLs
7. `dars.retrievalcore.jwt-secret` - JWT secret
8. `dars.store-path` - path where to store the extracted article information

II. Create the directory with the site information and extraction template (the `dars.config.site-info-path` property).
A sample directory is found at `RetrievalCore/sites`.
For each site, a JSON file must be created and it must have a specific format (see the provided sample).

III. Create a file containing the list of URLs for the other RetrievalCores in the environment (`dars.config.server-list-path` property), if there are any.

IV. Create a directory where to store the extracted article information. (`dars.store-path` property), if there are any.

V. Update the database with the credentials for the crawler and scraper instances.
Initially are registered one crawler and one scraper. Details in
`RetrievalCore/src/main/java/ro/h23/dars/retrievalcore/auth/persistence/loader/UserDataLoader.java`.

## WebCrawler configuration

Update the general configuration file - `WebCrawler/src/main/resources/application.yml` - and update at least the following:
1. `spring.datasource.url` - database connection URL
2. `spring.datasource.username` - username for connecting to the database
3. `spring.datasource.password` - password for connecting to the database
4. `crawler.site-wait-time-min` - minimum waiting time between crawls from the same site (in milliseconds)
5. `crawler.site-wait-time-max` - maximum waiting time between crawls from the same site (in milliseconds)
6. `retrievalcore-api.server` - RetrievalCore API server URL
7. `retrievalcore-api.authentication-path` - RetrievalCore API server authentication path 
8. `retrievalcore-api.username` - Username used for authenticating to the RetrievalCore API
9. `retrievalcore-api.password` - Password used for authenticating to the RetrievalCore API

## WebScraper configuration

Update the general configuration file - `WebCrawler/src/main/resources/application.yml` - and update at least the following:
1. `spring.datasource.url` - database connection URL
2. `spring.datasource.username` - username for connecting to the database
3. `spring.datasource.password` - password for connecting to the database
4. `scraper.site-wait-time-min` - minimum waiting time between crawls from the same site (in milliseconds)
5. `scraper.site-wait-time-max` - maximum waiting time between crawls from the same site (in milliseconds)
6. `scraper.output-dir` - path to the output directory that stores the article information and featured image
7. `retrievalcore-api.server` - RetrievalCore API server URL
8. `retrievalcore-api.authentication-path` - RetrievalCore API server authentication path
9. `retrievalcore-api.username` - Username used for authenticating to the RetrievalCore API
10. `retrievalcore-api.password` - Password used for authenticating to the RetrievalCore API

## Running each component

The easiest way to run the three components is executing `mvn spring-boot:run` from their corresponding directory.

The alternative is to create a JAR file for each of three components (`mvn package`) and then executing it with the `java` command.

