# InStoreRobot

## Table of Contents

- [Project Description](#project-description)
- [Features](#features)
- [Installation](#installation)
- [Usage](#usage)
- [License](#license)
- [Acknowledgments](#acknowledgments)
- [Contact](#contact)

## Project Description
This project was designed for a robot grocery cart that locates items within the store for online order fulfillment using three steps.

  1. Get Target Image

      (Traditionaly the store would provide the database)

     <img src=https://github.com/tatemouser/InStoreRobot/assets/114375692/a6b90612-6a03-41f8-9e93-bb39d2c546ac alt="Original Image" width="175" height="225">
     
          It takes input like "12 pack of Pepsi," which is then searched on Google using JSOUP. Fifteen images are scanned to find the best match for this search.
     
  3. Cleaning Target Image

     <img src=https://github.com/tatemouser/InStoreRobot/assets/114375692/a6b90612-6a03-41f8-9e93-bb39d2c546ac alt="Original Image" width="175" height="225">

          The image is then scanned using OpenCV to locate three points on each side, creating a box region used to remove the remaining areas outside.
     
  5. Image Matching
      (Traditionaly the camera would provide the 2nd image)

     <img src=https://github.com/tatemouser/InStoreRobot/assets/114375692/d890bc16-2809-4350-acf7-ac993f6ba038 alt="Original Image" width="500" height="300">

         The green box drawn comes from the target item and the store shelf image being scanned to identify their keypoints (patterns, edges, corners). The program matches the keypoints and applies a homography matrix to account for tilt in the image of the item or the camera. Once complete, the program draws a green box around the located item and displays the result using the SWT
     

## Features

- Capable or using web searched image or downloaded image.
- URL searching.
- Read RGB color values.
- Generate a descriptor / keypoint matrix.
- Homography alligning.

## Installation
Packages required JSOUP, OPENCV, SWT

## Usage
When running, the program will print the initialization and completion of each step to the console, stoppping everything if one step does not produce a valid output.

Crawler

> Consists of two steps, the first takes in the item name string for a google search. When iterating through the first 15 images, the program looks for a new URL that contains "kroger" as most food items linking to kroger's website are more defined with empty white backgrounds. Once an image is found, the URL to it is passed into the downloadImageAndConvertToMat method to generate a mat that can be used later.

RemoveBackground

> Since the image will have an all white background taken from krogers website, the CompareItem method used throughout this class determines that the color is white if the RGB values do not differ by more than 25 points of intensity. Knowing this, the program scans towards the center of the image 3 times on each side, only stopping when the color stops being white. Now the image has 9 points of border. Taking the outermost point on each side, the program stores 4 points that can be drawn to intersection that create a region of interest rectangle. A new Mat is created with the new points that returns a smaller image of the item to the Index class. 

SurfDetector (Speeded-Up Robust Features)

> The SURFDetector algorithm is designed to find a specific object in a scene using the SURF feature detection and matching technique. It takes an object image and a shelf image as inputs, locating the object's position and orientation in the scene. The algorithm detects keypoints and computes feature descriptors for both images, matching them to find good matches using a distance ratio test. By calculating a homography matrix, the algorithm transforms the object's corners to their corresponding positions in the scene, effectively aligning the object with its correct location. The result is visually represented by drawing green rectangles around the identified object in the scene image.
  
## License
None.

## Acknowledgments
Remastered the original SURFDetector class for this project from Kinathru. [https://github.com/kinathru]

## Contact
tatesmouser@gmail.com
