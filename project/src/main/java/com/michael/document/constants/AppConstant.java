package com.michael.document.constants;

public class AppConstant {
    public static final String BASE_PATH = "/**";
    public static final String USER_LOGIN = "/user/login";
    public static final int NINETY_DAYS = 90;
    public static final int STRENGTH = 12;
    public static final String MICHAEL_APP = "Michael_App";
    public static final String MICHAEL_ROYF_LLC = "MICHAEL_ROYF_LLC";
    public static final String EXISTING_PASSWORD_INCORRECT = "Existing password is incorrect";


    public static final String ROLE = "role";
    public static final String ROLE_PREFIX = "ROLE_";
    public static final String AUTHORITY_DELIMITER = ",";
    public static final String EMPTY_VALUE = "empty";
    public static final String AUTHORITIES = "authorities";

    public static final String FILE_NAME = "File-Name";


    public static final String[] PUBLIC_ROUTES = {"/user/reset_password/reset",
            "/user/verify/reset_password",
            "/user/reset_password",
            "/user/verify/qrcode",
            "/user/stream",
            "/user/id",
            "/user/login",
            "/user/register",
            "/user/new/password",
            "/user/verify",
            "/user/refresh/token",
            //   "/user/image",
            "/user/verify/account",
            "/user/verify/password",
            "/user/verify/code"};

    public static final String[] PUBLIC_URLS = {
            "/user/reset_password/reset/**",
            "/user/reset_password/**",
            "/user/reset_password/**",
            "/user/verify/**",
            "/user/verify/qrcode/**",
            "/user/verify/reset_password/**",
            "/user/verify/account/**",
            "/user/verify/password/**",
            "/user/login/**",
            "/user/register/**",
            "/user/new/password/**",
            //  "document/download/**",
            //   "/user/image/**",
            //     "/user/mfa/setup/**"
    };

    public static final String[] SWAGGER_PUBLIC_URLS = {
            "/v3/api-docs/**",
            "/swagger-resources",
            "/swagger-resources/**",
            "/configuration/ui",
            "/configuration/security",
            "/swagger-ui/**",
            "/webjars/**",
            "/swagger-ui.html"
    };


    public static final String USER_AUTHORITIES = "document:create,document:read,document:update,document:delete";
    public static final String ADMIN_AUTHORITIES = "user:create,user:read,user:update,document:create,document:read,document:update,document:delete";
    public static final String SUPER_ADMIN_AUTHORITIES = "user:create,user:read,user:update,user:delete,document:create,document:read,document:update,document:delete";
    public static final String MANAGER_AUTHORITIES = "document:create,document:read,document:update,document:delete";


    public static final String USERNAME_ALREADY_EXISTS = "Username already exists";
    public static final String EMAIL_ALREADY_EXISTS = "Email already exists";
    public static final String NO_USER_FOUND_BY_USERNAME = "No user found by username: %s";
    public static final String NO_USER_FOUND_BY_EMAIL = "No user found by email: %s ";
    public static final String NO_USER_FOUND_BY_ID = "No user found by ID: %s";
    public static final String NO_ROLE_FOUND_BY_NAME = "No role found by name: %s";
    public static final String NOT_FOUND = "Not found";
    public static final String INCORRECT_PASSWORD = "Incorrect password";
    public static final String USER_DELETED = "User with username: %s was deleted";
    public static final String USER_CREDENTIAL_NOT_FOUND = "Unable to find user credential";
    public static final String TOKEN_NOT_FOUND = "Unable to find token";
    public static final String INCORRECT_TOKEN = "Incorrect token";
    //   public static final String EXISTING_PASSWORD_INCORRECT = "Existing password is incorrect";
    public static final String INVALID_QR_CODE = "Invalid QR code. Please try again";
    public static final String CONFIRMATION_INFORMATION_NOT_FOUND = "Confirmation information not found";


    //pagination
    public static final String DEFAULT_PAGE_NUMBER = "0";
    public static final String DEFAULT_PAGE_SIZE = "10";
    public static final String DEFAULT_SORT_BY = "name";
    public static final String DEFAULT_SORT_DIRECTION = "asc";


    //
    public static final String USER_AVATAR_PATH = "/avatar/";
    public static final String JPG_EXTENSION = "jpg";
    public static final String PNG_EXTENSION = ".png";
    public static final String DOT = ".";
    public static final String FORWARD_SLASH = "/";
    public static final String NOT_AN_IMAGE_FILE = "%s is not an image file. Please upload an image file";
    public static final String TEMP_AVATAR_BASE_URL = "https://robohash.org/";
    //   public static final String PATH_PREFIX = "/api/v1/";
    public static final String AVATAR_NOT_FOUND = "Avatar not found";
    public static final String NO_AVATAR_FOUND_BY_FILENAME = "No avatar found by filename: %s";
    public static final String NO_AVATAR_FOUND_BY_URL = "No avatar found by URL: %s";
    public static final String NO_AVATAR_BY_USER = "No avatar found by user: %s";
    //"https://cdn-icons-png.flaticon.com/512/149/149071.png"
}
