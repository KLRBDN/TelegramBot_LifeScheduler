package BotCommands;

import DateStructure.Day;
import DialogueHandling.BotRequest;
import DialogueHandling.KeyboardConfiguration;
import DialogueHandling.StandardBotRequest;
import TaskConfiguration.Task;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.time.LocalDate;
import java.util.ArrayList;

public class GetTasks implements BotCommand {

    @Override
    public String getDescription() {
        return "Возвращает все активные задачи на выбранную дату";
    }

    @Override
    public String getName() {
        return "/tasks";
    }

    @Override
    public BotRequest exec(Update answer) {
        var message = KeyboardConfiguration.createMessageWithCalendarKeyboard(answer.getMessage().getChatId());
        return new BotRequest(message, this::processAnswer);
    }

    private BotRequest processAnswer(Update answer) {
        var date = answer.getCallbackQuery().getData();
        var tasks = processDateAndGetTasks(date);
        var strBuilder = new StringBuilder();
        if (tasks != null) {
            for (Task task : tasks)
                strBuilder.append(task.name)
                        .append(": ")
                        .append(task.timeInterval.toString())
                        .append("\n");
            if (strBuilder.length() == 0)
                return new StandardBotRequest("No tasks for this date");
            return new StandardBotRequest(strBuilder.toString());
        }
        return new StandardBotRequest("No tasks for this date");
    }

    private ArrayList<Task> processDateAndGetTasks(String date) {
        try {
            return getDayTasks(date);
        } catch (NullPointerException e) {
            return null;
        }
    }

    public static ArrayList<Task> getTodayTasks() {
        return Day.getToday().getTasks();
    }

    public static ArrayList<Task> getDayTasks(String date) {
        return Day.getDay(date).getTasks();
    }

    public static ArrayList<Task> getDayTasks(LocalDate date) {
        return Day.getDay(date).getTasks();
    }

    public static ArrayList<Task> getDayTasks(int day, int month, int year) {
        var date = Day.getDay(day, month, year);
        if (date != null)
            return date.getTasks();
        return null;
    }
}
