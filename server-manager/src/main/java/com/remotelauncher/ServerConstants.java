/*
 * REMOTE TASK LAUNCHER
 *
 * Developed by Nikolay Komarov and Rostislav Povelikin
 * UNN, 2017
 */
package com.remotelauncher;

public abstract class ServerConstants {

    public static final int PORT_NUMBER = 59342;
    public static final int WORK_THREAD_THRESHOLD = 5;
    public static final String PATH_TO_TASKS = "C:\\temp\\";
    //public static final String PATH_TO_TASKS = System.getProperty("user.home") + "\\AppData\\Local\\ServerManager\\";

    public static final String TYPE = "type";
    public static final String CLIENT_STATE = "clientState";
    public static final String SERVER_STATE = "serverState";
    public static final String MESSAGE = "message";

    public static final String USER_NAME = "token";
    public static final String PASSWORD = "password";

    public static final String USER_ID = "userId";
    public static final String TASK_NAME = "taskName";
    public static final String TASK_ID = "taskId";
    public static final String TASK_IS_COMPLETED = "taskIsCompleted";
    public static final String TASK_USER_ID = "taskUserId";
    public static final String TASK_FILE = "taskFile";
    public static final String TASK_FILE_SIZE = "taskFileSize";
    public static final String TASK_FORMAT_TYPE = "taskFormatType";
    public static final String QUEUE = "queue";
    public static final String TASK_SESSION = "taskSession";

    public static final String SHOW_MY_TASKS_ONLY = "showMyTasksOnly";
    public static final String SHOW_UNCOMPLETED_TASKS = "showUncompletedTasks";
}
