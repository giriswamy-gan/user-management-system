# API Specification

This document provides an overview of all the APIs exposed.

---

## Base URL : /journal


---

## Endpoints

### 1. **Get All Journal Entries**

- **Endpoint**: `/all`
- **Method**: `GET`
- **Description**: Retrieves all journal entries.
- **Authorization**:
  - Requires the `ADMIN` role.
- **Response**:
  - **Success**:
    - **Status Code**: `200 OK`
    - **Body**:
      ```json
      {
        "status": "success",
        "message": "Journal Entries Retrieved",
        "data": [
          {
            "id": "string",
            "action": "string",
            "timestamp": "string",
            "payload": "string"
          }
        ]
      }
      ```
  - **Error**:
    - **Status Code**: `401 UNAUTHORIZED`
    - **Body**:
      ```json
      {
        "path": "/journal/all",
        "error": "Forbidden",
        "message": "Access denied: You don't have permission to access this resource",
        "timestamp": "2025-05-04T17:10:26.592883755",
        "status": 401
      }
      ```
  - **Error**:
    - **Status Code**: 500 INTERNAL_SERVER_ERROR
    - **Body**:
      ```json
      {
      "status": "STATUS_CODE",
      "data": {
        "message": "Error Message",
        "customCode": "CUSTOM_CODE"
       }
      }
      ```

---

### 2. **Get Journal Entries Based on Action**

- **Endpoint**: `/`
- **Method**: `GET`
- **Description**: Retrieves a list of journal entries filtered by the specified action.
- **Authorization**:
  - Requires the `ADMIN` role.
- **Query Parameters**:
  - action (required): The action parameter used to filter journal entries.
    - **Type**: `string`
    - **Example**: `CREATED`
- **Response**:
  - **Success**:
    - **Status Code**: 200 OK
    - **Body**:
      ```json
      {
        "status": "success",
        "message": "Journal Entries",
        "data": [
          {
            "id": "string",
            "action": "string",
            "timestamp": "string",
            "payload": "string"
          }
        ]
      }
      ```
  - **Error**:
    - **Status Code**: `401 UNAUTHORIZED`
    - **Body**:
      ```json
      {
        "path": "/journal/all",
        "error": "Forbidden",
        "message": "Access denied: You don't have permission to access this resource",
        "timestamp": "2025-05-04T17:10:26.592883755",
        "status": 401
      }
      ```
  - **Error**:
    - **Status Code**: 500 INTERNAL_SERVER_ERROR
    - **Body**:
      ```json
      {
      "status": "STATUS_CODE",
      "data": {
        "message": "Error Message",
        "customCode": "CUSTOM_CODE"
       }
      }
      ```

---

## Error Handling

- **500 INTERNAL_SERVER_ERROR**: Returned when an exception occurs while processing the request.
  - **Body**:
    ```json
    {
      "status": "STATUS_CODE",
      "data": {
        "message": "Error Message",
        "customCode": "CUSTOM_CODE"
       }
    }
    ```

---

## Base URL : /users


---

## Endpoints

### 1. **Get All Users**

- **Endpoint**: `/all`
- **Method**: `GET`
- **Description**: Retrieves a list of all users.
- **Response**:
  - **Success**:
    - **Status Code**: `200 OK`
    - **Body**:
      ```json
      {
        "status": "success",
        "message": "Successfully retrieved users",
        "data": [
          {
            "id": "string",
            "name": "string",
            "email": "string",
            "roles": ["string"]
          }
        ]
      }
      ```
  - **Error**:
    - **Status Code**: `401 UNAUTHORIZED`
    - **Body**:
      ```json
      {
        "status": 401,
        "data": {
            "message": "Invalid Token",
            "customCode": "INVALID_TOKEN"
        }
      }
      ```
  - **Error**:
    - **Status Code**: `500 INTERNAL_SERVER_ERROR`
    - **Body**:
      ```json
      {
      "status": "STATUS_CODE",
      "data": {
        "message": "Error Message",
        "customCode": "CUSTOM_CODE"
       }
      }
      ```

---

### 2. **Get User by ID**

- **Endpoint**: `/{userId}`
- **Method**: `GET`
- **Description**: Retrieves a user by their unique identifier.
- **Path Parameters**:
  - userId (required): The unique identifier of the user.
    - **Type**: `string`
    - **Example**: `12345`
- **Response**:
  - **Success**:
    - **Status Code**: 200 OK
    - **Body**:
      ```json
      {
        "status": "success",
        "message": "Successfully retrieved user",
        "data": {
          "id": "string",
          "name": "string",
          "email": "string",
          "roles": ["string"]
        }
      }
      ```
  - **Error**:
    - **Status Code**: `400 BAD_REQUEST`
    - **Body**:
      ```json
      {
        "status": 400,
        "data": {
            "message": "User not found",
            "customCode": "USER_NOT_FOUND"
        }
      }
      ```
  - **Error**:
    - **Status Code**: `401 UNAUTHORIZED`
    - **Body**:
      ```json
      {
        "status": 401,
        "data": {
            "message": "Invalid Token",
            "customCode": "INVALID_TOKEN"
        }
      }
      ```
  - **Error**:
    - **Status Code**: `500 INTERNAL_SERVER_ERROR`
    - **Body**:
      ```json
      {
      "status": "STATUS_CODE",
      "data": {
        "message": "Error Message",
        "customCode": "CUSTOM_CODE"
       }
      }
      ```

---

### 3. **Update User**

- **Endpoint**: `/{userId}`
- **Method**: `PUT`
- **Description**: Updates the details of an existing user.
- **Path Parameters**:
  - userId (required): The unique identifier of the user to be updated.
    - **Type**: `string`
    - **Example**: `12345`
- **Request Body**:
  - **Type**: `
`application/json  - **Example**:
    ```json
    {
      "username": "Updated Username",
      "name": "Updated Name",
      "password": "Updated Password",
      "roles": ["USER", "ADMIN"]
    }
    ```
- **Response**:
  - **Success**:
    - **Status Code**: `200 OK`
    - **Body**:
      ```json
      {
        "status": "success",
        "message": "Successfully updated user",
        "data": "12345"
      }
      ```
  - **Error**:
    - **Status Code**: `400 BAD_REQUEST`
    - **Body**:
      ```json
      {
        "status": 400,
        "data": {
            "message": "User not found",
            "customCode": "USER_NOT_FOUND"
        }
      }
      ```
  - **Error**:
    - **Status Code**: `401 UNAUTHORIZED`
    - **Body**:
      ```json
      {
        "status": 401,
        "data": {
            "message": "Invalid Token",
            "customCode": "INVALID_TOKEN"
        }
      }
      ```
  - **Error**:
    - **Status Code**: `500 INTERNAL_SERVER_ERROR`
    - **Body**:
      ```json
      {
      "status": "STATUS_CODE",
      "data": {
        "message": "Error Message",
        "customCode": "CUSTOM_CODE"
       }
      }
      ```

---

### 4. **Delete User**

- **Endpoint**: `/{userId}`
- **Method**: `DELETE`
- **Description**: Deletes a user by their unique identifier.
- **Path Parameters**:
  - userId (required): The unique identifier of the user to be deleted.
    - **Type**: `string`
    - **Example**: `12345`
- **Authorization**:
  - Requires the `ADMIN` role.
- **Response**:
  - **Success**:
    - **Status Code**: 200 OK
    - **Body**:
      ```json
      {
        "status": "success",
        "message": "Successfully deleted user",
        "data": "12345"
      }
      ```
  - **Error**:
    - **Status Code**: `400 BAD_REQUEST`
    - **Body**:
      ```json
      {
        "status": 400,
        "data": {
            "message": "User not found",
            "customCode": "USER_NOT_FOUND"
        }
      }
      ```
  - **Error**:
    - **Status Code**: `401 UNAUTHORIZED`
    - **Body**:
      ```json
      {
        "status": 401,
        "data": {
            "message": "Invalid Token",
            "customCode": "INVALID_TOKEN"
        }
      }
      ```
  - **Error**:
    - **Status Code**: `500 INTERNAL_SERVER_ERROR`
    - **Body**:
      ```json
      {
      "status": "STATUS_CODE",
      "data": {
        "message": "Error Message",
        "customCode": "CUSTOM_CODE"
       }
      }
      ```

---

## Error Handling

- **500 INTERNAL_SERVER_ERROR**: Returned when an exception occurs while processing the request.
  - **Body**:
    ```json
    {
      "status": "STATUS_CODE",
      "data": {
        "message": "Error Message",
        "customCode": "CUSTOM_CODE"
       }
    }
    ```

---

## Notes

- The SuccessResponse object wraps the response data and includes a `status` and `message` field for better clarity.
- The CustomApiException is used to handle application-specific errors and return meaningful error messages.
- The `@PreAuthorize` annotation ensures that only users with the `ADMIN` role can access certain endpoints.


## Base URL : /roles


---

## Endpoints

### 1. **Create Role**

- **Endpoint**: `/`
- **Method**: `POST`
- **Description**: Creates a new role in the system.
- **Request Body**:
  - **Type**: `application/json`
  - **Example**:
    ```json
    {
      "name": "ROLE_ADMIN",
    }
    ```
- **Response**:
  - **Success**:
    - **Status Code**: `201 CREATED`
    - **Body**:
      ```json
      {
        "status": 201,
        "data": {
            "message": "Role created",
            "data": "ROLE_ADMIN",
            "customCode": ""
        }
      }
      ```
  - **Error**:
    - **Status Code**: `400 BAD_REQUEST`
    - **Body**:
      ```json
      {
        "status": 400,
        "data": {
            "message": "Role name already exists",
            "customCode": "ROLE_NAME_EXISTS"
        }
      }
      ```
  - **Error**:
    - **Status Code**: `401 UNAUTHORIZED`
    - **Body**:
      ```json
      {
        "status": 401,
        "data": {
            "message": "Invalid Token",
            "customCode": "INVALID_TOKEN"
        }
      }
      ```
  - **Error**:
    - **Status Code**: `500 INTERNAL_SERVER_ERROR`
    - **Body**:
      ```json
      {
      "status": "500",
      "data": {
        "message": "Error Message",
        "customCode": "CUSTOM_CODE"
       }
      }
      ```

---

### 2. **Get All Roles**

- **Endpoint**: `/`
- **Method**: `GET`
- **Description**: Retrieves all roles from the system.
- **Response**:
  - **Success**:
    - **Status Code**: 200 OK
    - **Body**:
      ```json
      {
        "status": 200,
        "data": {
            "message": "All roles retrieved",
            "data": [
                {
                    "roleId": "rol-0268fb5a-5577-4de0-a8aa-4912b79f7c9c",
                    "roleName": "ROLE_ADMIN"
                },
                {
                    "roleId": "rol-45fa5fb2-2577-440a-96bc-97030b8d7172",
                    "roleName": "ROLE_USER"
                }
            ],
            "customCode": ""
        }
      }
      ```
  - **Error**:
    - **Status Code**: `401 UNAUTHORIZED`
    - **Body**:
      ```json
      {
        "status": 401,
        "data": {
            "message": "Invalid Token",
            "customCode": "INVALID_TOKEN"
        }
      }
      ```
  - **Error**:
    - **Status Code**: `500 INTERNAL_SERVER_ERROR`
    - **Body**:
      ```json
      {
      "status": "500",
      "data": {
        "message": "Error Message",
        "customCode": "CUSTOM_CODE"
       }
      }
      ```

---

### 3. **Get Role by ID**

- **Endpoint**: `/{roleId}`
- **Method**: `GET`
- **Description**: Retrieves a role by its unique identifier.
- **Path Parameters**:
  - roleId (required): The unique identifier of the role.
    - **Type**: `string`
    - **Example**: `12345`
- **Response**:
  - **Success**:
    - **Status Code**: 200 OK
    - **Body**:
      ```json
      {
        "status": "success",
        "message": "Role retrieved",
        "data": {
          "id": "string",
          "name": "string",
        }
      }
      ```
  - **Error**:
    - **Status Code**: `401 UNAUTHORIZED`
    - **Body**:
      ```json
      {
        "status": 401,
        "data": {
            "message": "Invalid Token",
            "customCode": "INVALID_TOKEN"
        }
      }
      ```
  - **Error**:
    - **Status Code**: `500 INTERNAL_SERVER_ERROR`
    - **Body**:
      ```json
      {
      "status": "STATUS_CODE",
      "data": {
        "message": "Error Message",
        "customCode": "CUSTOM_CODE"
       }
      }
      ```

---

### 4. **Update Role**

- **Endpoint**: `/{roleId}`
- **Method**: `PUT`
- **Description**: Updates an existing role with the provided details.
- **Path Parameters**:
  - roleId (required): The unique identifier of the role to be updated.
    - **Type**: `string`
    - **Example**: `12345`
- **Request Body**:
  - **Type**: `application/json`
  - **Example**:
    ```json
    {
      "name": "UPDATED_ROLE",
    }
    ```
- **Authorization**:
  - Requires the `ADMIN` role.
- **Response**:
  - **Success**:
    - **Status Code**: 200 OK
    - **Body**:
      ```json
      {
        "status": "success",
        "message": "Role updated",
        "data": "UPDATED_ROLE"
      }
      ```
  - **Error**:
    - **Status Code**: `401 UNAUTHORIZED`
    - **Body**:
      ```json
      {
        "status": 400,
        "data": {
            "message": "Role not found: ROLE_US",
            "customCode": "ROLE_NOT_FOUND"
        }
      }
      ```
  - **Error**:
    - **Status Code**: `401 UNAUTHORIZED`
    - **Body**:
      ```json
      {
        "status": 401,
        "data": {
            "message": "Invalid Token",
            "customCode": "INVALID_TOKEN"
        }
      }
      ```
  - **Error**:
    - **Status Code**: `500 INTERNAL_SERVER_ERROR`
    - **Body**:
      ```json
      {
      "status": "STATUS_CODE",
      "data": {
        "message": "Error Message",
        "customCode": "CUSTOM_CODE"
       }
      }
      ```

---

## Error Handling

- **500 INTERNAL_SERVER_ERROR**: Returned when an exception occurs while processing the request.
  - **Body**:
    ```json
    {
      "status": "500",
      "data": {
        "message": "Error Message",
        "customCode": "CUSTOM_CODE"
       }
    }
    ```

---


## Base URL : /auth


---

## Endpoints

### 1. **Register User**

- **Endpoint**: `/register`
- **Method**: `POST`
- **Description**: Registers a new user in the system.
- **Request Body**:
  - **Type**: `application/json`
  - **Example**:
    ```json
    {
      "username": "john_doe",
      "password": "securepassword",
      "fullName": "John Doe",
      "roleName": ["ROLE_USER", "ROLE_ADMIN"]
    }
    ```
- **Response**:
  - **Success**:
    - **Status Code**: `201 CREATED`
    - **Body**:
      ```json
      {
        "status": 201,
        "data": {
            "message": "User registered",
            "data": "John Doe",
            "customCode": ""
        }
      }
      ```
  - **Error**:
    - **Status Code**: 409 CONFLICT
      - **Reason**: Username already taken.
      - **Body**:
        ```json
        {
          "status": 409,
          "data": {
              "message": "Username already taken",
              "customCode": "USERNAME_TAKEN"
          }
        }
        ```
    - **Status Code**: 400 BAD_REQUEST
      - **Reason**: Role not found.
      - **Body**:
        ```json
        {
          "status": 400,
          "data": {
              "message": "Role not found: ROLE_USER",
              "customCode": "ROLE_NOT_FOUND"
          }
        }
        ```

---

### 2. **Login User**

- **Endpoint**: `/login`
- **Method**: `POST`
- **Description**: Authenticates a user and generates a JWT token.
- **Request Body**:
  - **Type**: `application/json`
  - **Example**:
    ```json
    {
      "username": "john_doe",
      "password": "securepassword"
    }
    ```
- **Response**:
  - **Success**:
    - **Status Code**: `202 ACCEPTED`
    - **Body**:
      ```json
      {
        "status": 202,
        "data": {
            "message": "Token generated",
            "data": "jwt_token_here",
            "customCode": ""
        }
      }
      ```
  - **Error**:
    - **Status Code**: 401 UNAUTHORIZED
      - **Reason**: Invalid username or password.
      - **Body**:
        ```json
        {
          "status": 401,
          "data": {
              "message": "Invalid username or password",
              "data": "HII",
              "customCode": ""
          }
        }
        ```

---

## Error Handling

- **400 BAD_REQUEST**: Returned when a role specified in the registration request does not exist.
- **401 UNAUTHORIZED**: Returned when login credentials are invalid.
- **409 CONFLICT**: Returned when the username is already taken during registration.
- **500 INTERNAL_SERVER_ERROR**: Returned when an unexpected error occurs.

---

## Notes

- The KafkaService sends user-related events to Kafka for further processing.
- If no roles are provided during registration, the default role `ROLE_USER` is assigned.
