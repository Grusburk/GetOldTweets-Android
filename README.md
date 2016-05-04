# Get Old Tweets Programatically
A project written in Java and ported for android to get old tweets, it bypass some limitations of Twitter Official API.

## Details
Twitter Official API has the bother limitation of time constraints, you can't get older tweets than a week. Some tools provide access to older tweets but in the most of them you have to spend some money before.
I was searching other tools to do this job but I didn't found it, so after analyze how Twitter Search through browser works I understand its flow. Basically when you enter on Twitter page a scroll loader starts, if you scroll down you start to get more and more tweets, all through calls to a JSON provider. After mimic we get the best advantage of Twitter Search on browsers, it can search the deepest oldest tweets.

## Components
- **Tweet:** Model class to give some informations about a specific tweet.
  - username (String)
  - text (String)
  - date (Date)
  - avatarUrl (String)
  ### To be implemented
  - id (String)
  - permalink (String)
  - retweets (int)
  - favorites (int)
  - mentions (String)
  - hashtags (String)
  - geo (String)

- **TweetManager:** A manager class to help getting tweets in **Tweet**'s model.
  - executeTwitterQuery (**Query**,**limit**,**callback**): callback the list of tweets retrieved by using the given query and limit.
  **The limit is experimental at this stage and will return a slightly higher number than what is given** 
