package TaskConfiguration;

import javax.management.InvalidAttributeValueException;

public class Task {
    public TimeInterval timeInterval;
    public TaskType taskType;
    public String name;
    public String description;

    public Task(Time start, Time end, TaskType taskType, String name, String description) throws InvalidAttributeValueException {
        super();
        if (start.gt(end))
            throw new InvalidAttributeValueException("Start is bigger than end");
        this.timeInterval = new TimeInterval(start, end);
        this.taskType = taskType;
        this.name = name;
        this.description = description;
    }
}
