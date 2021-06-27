package ru.svetlov.domain.command.base;

public class Commands {
  public static final String LIST_FILES = "list_files";
  public static final String LIST_FILES_UPDATE = "list_files_update";
  public static final String LOGIN_REPLY = "login_reply";
  public static final String LOGIN_REQUEST = "login_request";
  public static final String LOGIN_REQUIRED = "login_required";
  public static final String INVALID_REQUEST = "request_invalid";
  public static final String REQUEST_PROCESSING = "request_processing";
  public static final String REQUEST_FILE_UPLOAD = "upload_file_request";
  public static final String REPLY_UPLOAD_PROCEED = "upload_file_proceed";
  public static final String REPLY_UPLOAD_FILE = "upload_file_contents";
  public static final String REPLY_UPLOAD_DONE = "upload_file_done";
  public static final String REPLY_UPLOAD_RETRY = "upload_file_retry";
  public static final String REPLY_UPLOAD_CHUNKS = "upload_file_chunks";
  public static final String REQUEST_UPLOAD_CHUNKS = "upload_chunks_request";
  public static final String REPLY_UPLOAD_CHUNKS_DONE = "upload_chunks_done";
  public static final String REPLY_UPLOAD_CHUNKS_FAIL = "upload_chunks_fail";

}
