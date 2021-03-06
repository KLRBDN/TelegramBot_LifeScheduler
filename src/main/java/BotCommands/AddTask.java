package BotCommands;

import javax.management.InvalidAttributeValueException;

import DateStructure.Day;
import DialogueHandling.BotRequest;
import DialogueHandling.KeyboardConfiguration;
import DialogueHandling.StandardBotRequest;
import TaskConfiguration.Task;
import TaskConfiguration.TaskType;
import TaskConfiguration.Time;
import TaskConfiguration.TimeInterval;
import org.example.*;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

public class AddTask implements BotCommand {
    private String date;
    protected TimeInterval timeInterval;
    protected String name;
    protected String description;

    @Override
    public String getName() {
        return "/add";
    }

    @Override
    public String getDescription() {
        return "Добавляет задачу на выбранную дату";
    }

    @Override
    public BotRequest exec(Update answer) {
        var message = KeyboardConfiguration.createMessageWithCalendarKeyboard(answer.getMessage().getChatId());
        return new BotRequest(message, this::askTimeInterval);
    }

    private BotRequest askTimeInterval(Update answer) {
        date = answer.getCallbackQuery().getData();
        var botRequest = new SendMessage();
        botRequest.setText("Write time interval of your task in format: 9:00 - 10:00");
        botRequest.setChatId(Long.toString(answer.getCallbackQuery().getMessage().getChatId()));
        return new BotRequest(botRequest, this::askTaskName);
    }

    private BotRequest askTaskName(Update answerWithTimeInterval) {
        timeInterval = processTimeInterval(answerWithTimeInterval);
        if (timeInterval != null)
            return new BotRequest("Write name for your task", this::askTaskDescription, LifeSchedulerBot.messageId);
        return new BotRequest(
                "Error: Wrong time, please try again and write " +
                        "time interval of your task in format: 9:00 - 10:00",
                this::askTaskName, LifeSchedulerBot.messageId);
    }

    protected BotRequest askTaskDescription(Update answerWithName) {
        this.name = answerWithName.getMessage().getText();
        return new BotRequest("Write description for your task", this::askTaskType, LifeSchedulerBot.messageId);
    }

    protected BotRequest askTaskType(Update answerWithDescription) {
        this.description = answerWithDescription.getMessage().getText();
        return new BotRequest(
                "Write 1 if your task is overlapping, 2 if nonOverlapping and 3 if important",
                this::processAnswer, LifeSchedulerBot.messageId);
    }

    protected BotRequest processAnswer(Update answerWithTaskType) {
        if (processAnswerForTaskType(answerWithTaskType)) {
            return new StandardBotRequest("Task was added");
        }
        return new BotRequest(
                "Error: Wrong value for task type. Please try again and" +
                        " write 1 if your task is overlapping, 2 if nonOverlapping and 3 if important",
                this::processAnswer, LifeSchedulerBot.messageId);
    }

    private TimeInterval processTimeInterval(Update answer) {
        var splitted = answer
                .getMessage()
                .getText()
                .split(" - ");
        if (splitted.length != 2)
            return null;

        return makeTimeInterval(splitted[0], splitted[1]);
    }

    protected TimeInterval makeTimeInterval(String start, String end) {
        var splStart = start.split(":");
        var splEnd = end.split(":");
        if (splStart.length != 2 || splEnd.length != 2)
            return null;
        try {
            return new TimeInterval(
                    new Time(Integer.parseInt(splStart[0]), Integer.parseInt(splStart[1])),
                    new Time(Integer.parseInt(splEnd[0]), Integer.parseInt(splEnd[1]))
            );
        } catch (InvalidAttributeValueException e) {
            return null;
        }
    }

    protected Boolean processAnswerForTaskType(Update answerWithTaskType) {
        int taskTypeAsInt;
        try {
            taskTypeAsInt = Integer.parseInt(answerWithTaskType.getMessage().getText());
        } catch (NumberFormatException e) {
            return false;
        }
        switch (taskTypeAsInt) {
            case 1:
                return addTask(TaskType.overlapping);
            case 2:
                return addTask(TaskType.nonOverlapping);
            case 3:
                return addTask(TaskType.important);
            default:
                return false;
        }
    }

    protected Task makeTask(TaskType taskType){
        try {
            return new Task(timeInterval.getStart(), timeInterval.getEnd(), taskType, name, description);
        } catch (InvalidAttributeValueException e) {
            return null;
        }
    }

    protected Boolean addTask(TaskType taskType) {
        var task = makeTask(taskType);
        if (task == null)
            return false;
        var day = Day.getDay(date);
        return day != null ? day.tryAddTask(task) : false;
    }
}