# Online Jeans Co 
Armin Keyvanloo

armin.keyvanloo@gmail.com

07907983166557



## A brief description of the functionality
The server currently serves the contents of orders-2016.csv to the frontend. This file contains around 25000 lines of orders (for the year 2016).
  The front-end allows viewing the summary of these orders in various shapes. 
  The data is represented in the following 6 categories:
  1. Top Manufacturers
  2. Top Selling Countries
  3. Top Selling Colours
  4. Top Selling Months
  5. Top Selling Sizes
  6. Top Selling Styles
  
  These can be accessed either from the home page or via the menubar at the top
  
  Once in any of these pages, filtering/grouping is provided via dropdowns. These filters are kept in root model so changing between pages keeps the filtered value(s).    
   
   N.B filters can be rest using the "Reset" button 

The data is displayed in a table within each of the pages. 

There is also a Pie chart representation of the data on each page that can be viewed by clicking on the "Graph" button. 

N.B The chart popup honors the selected filters.
 
 ### Run the application
 This project uses Play 2.5 for serving the data which depends on Java 8, so make sure you are using JVM 8.
 you also need to have sbt 0.13.11 installed. 
      
 In terminal, run the server by going to the root folder of the code and run the following: 
      
  ```
  sbt run
  ``` 
 
  Then you can access the application by going to [http://localhost:9000](http://localhost:9000)
      
 ### Run the tests
 To run the tests, you need to have NodeJs, PhantomJS and jsdom installed. 
 After installing node and its package manager npm you can install jsdom into your project folder with:
                                                             
 ```
 npm install jsdom
 npm install phantomjs
 ```
 
 Then you should be able to run the tests in terminal by running:
  
  ```
  sbt test
  ```
 

##  Reasons/justification for each library or framework used
1. [Scala.js](https://www.scala-js.org/): The reason Scala.js was chosen for this is to demonstrate the maturity and elegance of this new amazing tool and its eco-system as an alternative to vanilla javascript. I can talk about this point for a long time but I don't want to bore you :) 
2. [scalajs-react](https://github.com/japgolly/scalajs-react): React.js and scala.js are a prefect fit as React.js is just a rendering library (not a framework). 
3. [diode](https://github.com/suzaku-io/diode): Diode makes Scalajs-React even nicer to use by managing the application model, actions, effects (events) and view in a totally type safe manner.  
(Diode is a Scala/Scala.js library for managing immutable application state with unidirectional data flow. It is heavily influenced and inspired by Flux and Elm architectures, and libraries like Om and Redux)
4. [Boopickle](https://github.com/suzaku-io/boopickle): Binary serialization library for efficient network communication

##  Reasons for your choice of data format and protocol for the API. If you chose to use a simpler protocol than you would in production, please also explain the protocol you would use for this problem in production
BooPickle is the fastest and most size efficient serialization library that works on both Scala and Scala.js. It encodes into a binary format instead of the more customary JSON. A binary format brings efficiency gains in both size and speed, at the cost of legibility of the encoded data


##  Assumptions you made, if any, due to unclear requirements   
Even though all the application logic has been tested, I must confess that I ran out of time for getting the test coverage to near 100% for all the UI components. 

I also would like to give credit to many hard working scalajs community members. Special thanks to [Otto Chrons](https://github.com/ochrons) who has created the [scalajs-spa-tutorial](https://github.com/ochrons/scalajs-spa-tutorial) which I have used as the starting point and inspiration for this application.       