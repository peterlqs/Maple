# Maple
## Self-studying assisting application built for Vietnamese high school students
<!-- mình chỉ sẽ viết summary về app hiện tại của mình
vd giờ app mình mới đến nhập điểm rồi recommend bài học
-->

Maple is a mobile application that helps students perform better when self-studying.

Maple works as a virtual assistant. At first, it is required to enter the user’s current study progress. Once entered, it will tell a user whether he/she performs well or not. If not, it will suggest some practice videos related to certain subjects that the user needs to improve. Furthermore, users can chat with Maple if they need advice or better recommendations, or simply chat to get to know each other more.

Maple is fully developed with Android Studio.

This project was made for RMIT Tech Genius 2021 competition.
### I/ Study progress manager

**Provides detailed score analysis for students based on their goals.**

Users can input their score through typing or scanning if there is a template for it.

<p align="center">
  <img src="https://i.imgur.com/fwM45mm.png" width="350">
</p>

After users enter the scores, a summary will be available. All subjects that have scores below their desired score will be mentioned.

<p align="center">
  <img src="https://i.imgur.com/BUrw55c.png" width="350">
</p>

### II/ Videos recommendation system

**Recommends lessons for a specific individual, lessons will be grouped by subjects.**

<p align="center">
  <img src="https://i.imgur.com/702rZLi.png" width="350">
</p>

The screen above displays subjects that are currently under the favored score.

The curriculum in most Vietnamese schools are quite similar to each other. By knowing that, we collect the month when a specific chapter is being taught in the textbooks and store their subchapters in our database. Therefore, if the user has a subject that is below the favored score, the app will find the month that the user entered a score below the favored one, retrieve the lessons or videos about that month’s topic from the database and deliver them to the user.

### III/ Chatbot
#### STILL WIP

Our goal is to create a chatbot that can give advice and specific lessons to users. Even more, users can even have a little talk with it for fun.


<p align="center">
  <img src="https://i.imgur.com/6jTlhFB.jpg" width="350">
</p>

So far, our Bot has been developed mostly by BiLSTM with intents data self-collected.

*All made possible by some senior high school students at Le Hong Phong High school*
