## Table of Contents
- [Features](#features)
- [Assumptions](#assumptions)
- [SlideshowImage Entity](#slideshowimage-entity)
- [Endpoints](#endpoints)
    - [Image Endpoints](#image-endpoints)
    - [Slideshow Endpoints](#slideshow-endpoints)
- [Configuration](#configuration)
- [Setup and Running the Application](#setup-and-running-the-application)
- [Error Handling](#error-handling)
- [License](#license)

---

## Features

### Image Management:
- Add new images with validated URLs and duration.
- Reuse existing image records (by URL + duration).
- Delete images by ID.
- Search images by URL substring and/or duration (with pagination).

### Slideshow Management:
- Create and delete slideshows.
- Add duplicate images to the same slideshow.
- Maintain precise image ordering using `createdDate`.
- Retrieve ordered images from a slideshow.
- Record proof-of-play events for auditing.

---

## Assumptions
- Adding a slideshow with a **duplicate name** is **not allowed**.
- Adding an image with the **same URL and duration** will **reuse and update** the existing image.
- A slideshow **may include the same image multiple times**.
- The **order of images in a slideshow** is based on the time they were **added to the slideshow** (not the time they were uploaded).
- Once an image is deleted it will be deleted from the slideshow as well.


---

## SlideshowImage Entity

To support **duplicate image entries and ordering** in slideshows, the `SlideshowImage` join entity is used.

Each entry in `SlideshowImage` links a slideshow to an image with a timestamp, allowing:
- **Duplicate entries** (e.g., image 20 added multiple times).
- **Consistent ordering** using `createdDate`.

---

## Endpoints
- **Base URL example:** `http://localhost:8080`
#### Add Image
- **URL:** `/api/addImage`
- **Method:** `POST`

**Request:**
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

**Add the same URL + duration again:**
```json
{
  "url": "https://example.com/image25.jpg",
  "duration": 30
}
```
**Response (same ID returned):**
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

**Example:**
```
DELETE /api/deleteImage/101
```
**Response:**
```
"Image deleted successfully"
```

---

#### Search Images
- **URL:** `/api/images/search`
- **Method:** `GET`

**Examples:**
```
GET /api/images/search?url=example&duration=30&page=0&size=5
GET /api/images/search?duration=20
GET /api/images/search
```
**Sample Response:**
```json
[
  {
    "id": 20,
    "url": "https://example.com/image20.jpg",
    "duration": 30
  },
  {
    "id": 21,
    "url": "https://example.com/image21.jpg",
    "duration": 30
  }
]
```

---

### Slideshow Endpoints

#### Add Slideshow
- **URL:** `/api/addSlideshow`
- **Method:** `POST`

**Request (with duplicates):**
```json
{
  "name": "My Slideshow",
  "imageIds": [10, 11, 10]
}
```
**Response:**
```json
{
  "id": 3,
  "name": "My Slideshow",
  "imageIds": [10, 11, 10]
}
```

**Duplicate slideshow name:**
```json
{
  "name": "My Slideshow",
  "imageIds": [12, 13, 14]
}
```
**Response:**
```json
{
  "type": "about:blank",
  "title": "Internal Server Error",
  "status": 500,
  "detail": "Slideshow with Name My Slideshow is already exists.",
  "instance": "/api/addSlideshow"
}
```

---

#### Delete Slideshow
- **URL:** `/api/deleteSlideshow/{id}`
- **Method:** `DELETE`

**Example:**
```
DELETE /api/deleteSlideshow/1
```
**Response:**
```
"Slideshow deleted successfully"
```

---

#### Get Slideshow Order
- **URL:** `/api/slideShow/{id}/slidesShowOrder`
- **Method:** `GET`
- **Description:** Returns the ordered list of images based on their insertion timestamp.

**Example:**
```
GET /api/slideShow/1/slidesShowOrder?page=0&size=10
```
**Response with duplicate images:**
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

**Example:**
```
POST /api/slideShow/1/proof-of-play/101
```
**Response:**
```
"Proof of play recorded successfully"
```
**Response for invalid input:**
```json
{
  "type": "about:blank",
  "title": "Not Found",
  "status": 404,
  "detail": "Image with ID 233 not found.",
  "instance": "/api/slideShow/18/proof-of-play/233"
}
```
---

## Configuration

Edit `application.properties` in `src/main/resources`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/mydb
spring.datasource.username=root
spring.datasource.password=pass
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.jpa.hibernate.ddl-auto=update
spring.jpa.database-platform=org.hibernate.dialect.MySQL8Dialect
spring.jpa.show-sql=true
logging.level.org.hibernate.SQL=DEBUG
```

---

## Setup and Running the Application

```bash
git clone https://github.com/shiramenda/springboot-slideshow.git
cd springboot-slideshow
mvn clean install
mvn spring-boot:run
```

Use tools like Postman or cURL to test the API.

---

## Error Handling

The API uses `ProblemDetail` format for all exceptions:

| Exception                         | HTTP Status           | Example Message                                           |
|----------------------------------|------------------------|-----------------------------------------------------------|
| `ResourceNotFoundException`      | 404 NOT FOUND          | "Image with ID 99 not found."                            |
| `InvalidImageURLException`       | 400 BAD REQUEST        | "Invalid image URL: invalid.png"                         |
| `NoResultsFoundException`        | 404 NOT FOUND          | "No images found for the given criteria."               |
| `NoImagesFoundException`         | 404 NOT FOUND          | "No images found for slideshow ID: 1"                   |
| `DuplicateSlideshowNameException`| 500 INTERNAL SERVER    | "Slideshow with Name 'X' is already exists."            |
| `DatabaseOperationException`     | 500 INTERNAL SERVER    | "Database error while saving the image."                |
| `Exception` (uncaught)           | 500 INTERNAL SERVER    | "An unexpected error occurred."                         |

**Example response for missing image:**
```json
{
  "type": "about:blank",
  "title": "Not Found",
  "status": 404,
  "detail": "Image with ID 99 not found."
}
```

**Example response for invalid image URL:**
```json
{
  "type": "about:blank",
  "title": "Bad Request",
  "status": 400,
  "detail": "Invalid image URL: ftp://badlink.com"
}
```



