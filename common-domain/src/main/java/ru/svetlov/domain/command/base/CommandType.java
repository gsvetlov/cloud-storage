package ru.svetlov.domain.command.base;

public enum CommandType {
  UNKNOWN(""),
  LIST_FILES("list_files"),
  LIST_FILES_UPDATE("list_files_update"),
  LOGIN_REPLY("login_reply"),
  LOGIN_REQUEST("login_request"),
  LOGIN_REQUIRED("login_required"),
  INVALID_REQUEST("request_invalid"),
  REQUEST_PROCESSING("request_processing"),
  REQUEST_FILE_UPLOAD("upload_file_request"),
  REPLY_UPLOAD_PROCEED("upload_file_proceed"),
  REPLY_UPLOAD_FILE("upload_file_contents"),
  REPLY_UPLOAD_DONE("upload_file_done"),
  REPLY_UPLOAD_RETRY("upload_file_retry"),
  REPLY_UPLOAD_CHUNKS("upload_file_chunks"),
  REQUEST_UPLOAD_CHUNKS("upload_chunks_request"),
  REPLY_UPLOAD_CHUNKS_DONE("upload_chunks_done"),
  REPLY_UPLOAD_CHUNKS_FAIL("upload_chunks_fail");

  private final String command;

  CommandType(String command){
    this.command = command;
  }

  public String asString() {
    return command;
  }
}
