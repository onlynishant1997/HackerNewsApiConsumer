# HackerNewsApiConsumer

## API Endpoints -
1. GET /top-stories
2. GET /comments/{storyId}
3. GET /past-stories


## Docker Commands - 
1. docker build -f Dockerfile -t hacker-news-consumer.jar .
2. docker run -p 8082:8082 hacker-news-consumer