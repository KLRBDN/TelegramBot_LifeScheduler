package org.example;

import static org.junit.Assert.assertEquals;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

public class AddTaskCommandTest {
    @ParameterizedTest
    @CsvSource(delimiter='|', value= {
        "'10.10.2021 9:00 - 10:00'|true",
        "'1.1.2021 9:0 - 10:0'|true",
        "'29.02.2024 9:00 - 10:00'|true",
        "'29.02.2021 9:00 - 10:00'|false",
        "'10.10.2020 9:00 - 10:00'|false",
        "'10.10.2021 10:00 - 9:00'|false",
        "'10.10.2021 -1:00 - 10:00'|false",
        "'10.10.2021 9:70 - 10:00'|false",
        "'10.10.2021 9:00 - 25:00'|false",
        "'10.10.2021 9:00'|false",
        "'10.10.2021 9:00-10:00'|false",
        "'10.10.2021 9:00- 10:00'|false",
        "'10.10.2021 9:00 : 10:00'|false",
        "'10.2021 9:00 - 10:00'|false",
        "'-10.10.2021 9:00 - 10:00'|false",
        "'10.20.2021 9:00 - 10:00'|false",
        "'10.20.2021 9:00 - 10:00'|false",
    })
    public void addTaskCommandTest(String dateTime, Boolean ok){
        var chat = new Chat();
        chat.setId(1L);
        var myMessage = new Message();
        myMessage.setText(dateTime);
        myMessage.setChat(chat);
        var answer = new Update();
        answer.setMessage(myMessage);

        var taskCmd = new AddTask(YearsDateBase.getInstance());
        var handler1 = taskCmd.exec();

        assertEquals("write date and time in format: 10.10.2021 9:00 - 10:00", handler1.getLastBotMessage());

        var handler2 = handler1.handle(answer, null);

        if (ok) {
            assert(handler2 instanceof StandartAnswerHandler);
            assertEquals("task was added", handler2.getLastBotMessage());
        }
        else {
            assert(!(handler2 instanceof StandartAnswerHandler));
            assertEquals("write date and time in format: 10.10.2021 9:00 - 10:00", handler2.getLastBotMessage());
        }
        
    }
}