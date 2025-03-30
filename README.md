
---

# Image & Slideshow Management API

**Base URL:**  
All endpoints are prefixed with the base URL. For example, when running locally, the base URL might be:
```
http://localhost:8080
```

This project is a RESTful API designed to manage images and slideshows. It supports adding, searching, and deleting images as well as creating, ordering, and tracking slideshows.

## Table of Contents

- [Features](#features)
- [Endpoints](#endpoints)
    - [Image Endpoints](#image-endpoints)
        - [Add Image](#add-image)
        - [Delete Image](#delete-image)
        - [Search Images](#search-images)
    - [Slideshow Endpoints](#slideshow-endpoints)
        - [Add Slideshow](#add-slideshow)
        - [Delete Slideshow](#delete-slideshow)
        - [Get Slideshow Order](#get-slideshow-order)
        - [Record Proof-of-Play](#record-proof-of-play)
- [Configuration](#configuration)
- [Setup and Running the Application](#setup-and-running-the-application)
- [Error Handling](#error-handling)
- [License](#license)

## Features

- **Image Management:**
    - Add new images with a validated URL and duration.
    - Delete images by ID.
    - Search images by URL substring and/or duration with pagination.

- **Slideshow Management:**
    - Create and delete slideshows.
    - Retrieve ordered images within a slideshow.
    - Record proof-of-play events for images within a slideshow.

## Endpoints

### Image Endpoints

#### Add Image
- **URL:** `/api/addImage`
- **Method:** `POST`
- **Description:** Adds a new image. The image URL is validated to ensure it is of the correct format.

**Example Request Payload:**
```json
{
  "url": "https://example.com/image25.jpg",
  "duration": 30
}
```
**Response:**
```json
{
  "id": 25,
  "url": "https://example.com/image25.jpg",
  "duration": 30
}
```
**Example add the same url and duration and get the same id:**
```json
{
  "url": "https://example.com/image25.jpg",
  "duration": 30
}
```
**Response:**
```json
{
  "id": 25,
  "url": "https://example.com/image25.jpg",
  "duration": 30
}
```
---

#### Delete Image
- **URL:** `/api/deleteImage/{id}`
- **Method:** `DELETE`
- **Description:** Deletes an image by its ID.

**Example URL:**
```
http://localhost:8080/api/deleteImage/101
```

---

#### Search Images
- **URL:** `/api/images/search`
- **Method:** `GET`
- **Description:** Searches for images based on URL substring and/or duration. Supports pagination.

**Query Parameters:**
- `url` (optional): Substring to search within the image URL.
- `duration` (optional): Exact duration to filter images.
- Pagination parameters like `page` and `size` can be included.

**Example Request URL:**
```
http://localhost:8080/api/images/search
```
```
http://localhost:8080/api/images/search?url=example&duration=30&page=0&size=10
```
```
http://localhost:8080/api/images/search?page=0&size=5
```
---

### Slideshow Endpoints

#### Add Slideshow
- **URL:** `/api/addSlideshow`
- **Method:** `POST`
- **Description:** Creates a new slideshow and attaches images by their IDs.

**Example Request Payload:**
**Supporting duplicate images:**
```json
{
  "name": "Summer Vacation",
  "imageIds": [1, 2, 3]
}
```
```json
{
  "name": "duplicate image20 Vacation",
  "imageIds": [20,21,20]
}
```
**Example duplicate name:**
```json
{
  "name": "Summer Vacation",
  "imageIds": [4, 5, 6]
}
```
```json
{
  "type": "about:blank",
  "title": "Internal Server Error",
  "status": 500,
  "detail": "Slideshow with Name check id is already exists.",
  "instance": "/api/addSlideshow"
}
```

---

#### Delete Slideshow
- **URL:** `/api/deleteSlideshow/{id}`
- **Method:** `DELETE`
- **Description:** Deletes a slideshow by its ID.

**Example URL:**
```
http://localhost:8080/api/deleteSlideshow/1
```

---

#### Get Slideshow Order
- **URL:** `/api/slideShow/{id}/slidesShowOrder`
- **Method:** `GET`
- **Description:** Retrieves the images in the order they were added to a slideshow. Supports pagination.
- **Note:** the images will be displayed by the order they were created.
- If an image is included twice in the slideshow it will be displayed twice

**Example URL:**
```
http://localhost:8080/api/slideShow/1/slidesShowOrder?page=0&size=10
```
**Response, duplicated image20 will be displayed first:**
```json
[
  {
    "id": 20,
    "url": "https://example.com/image23.jpg",
    "duration": 30
  },
  {
    "id": 20,
    "url": "https://example.com/image23.jpg",
    "duration": 30
  },
  {
    "id": 21,
    "url": "https://example.com/image24.jpg",
    "duration": 30
  }
]
```
---

#### Record Proof-of-Play
- **URL:** `/api/slideShow/{id}/proof-of-play/{imageId}`
- **Method:** `POST`
- **Description:** Records a proof-of-play event indicating that a specific image has been played in a slideshow.

**Example URL:**
```
http://localhost:8080/api/slideShow/1/proof-of-play/101
```

---

## Configuration

Before running the application, update your `application.properties` file with the correct database details. Below is a sample configuration:

```properties
# Database Connection
spring.datasource.url=
spring.datasource.username=
spring.datasource.password=
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA / Hibernate Settings
spring.jpa.hibernate.ddl-auto=update
spring.jpa.database-platform=org.hibernate.dialect.MySQL8Dialect
spring.jpa.show-sql=true
logging.level.org.hibernate.SQL=DEBUG
```

**Note:** Make sure to change `spring.datasource.url`, `spring.datasource.username`, and `spring.datasource.password` to match your own database settings.

## Setup and Running the Application

1. **Clone the Repository:**
   ```bash
   git clone https://github.com/your-repo/image-slideshow-api.git
   cd image-slideshow-api
   ```

2. **Update Database Settings:**
   Modify the `application.properties` file located under `src/main/resources` with your specific database details.

3. **Build the Project:**
   Use Maven (or your preferred build tool) to build the project:
   ```bash
   mvn clean install
   ```

4. **Run the Application:**
   Start the application via your IDE or by running:
   ```bash
   mvn spring-boot:run
   ```

5. **Test the Endpoints:**
   Use tools like [Postman](https://www.postman.com/) or [cURL](https://curl.se/) to test the endpoints with the provided example payloads.

## Error Handling

The API returns meaningful error messages for various error scenarios:
- **Invalid Image URL:** When the image URL doesn't match the expected format.
- **Resource Not Found:** When an image or slideshow is not found.
- **No Results Found:** When no images match the search criteria.
- **Database Errors:** When issues occur during save or delete operations.

---

