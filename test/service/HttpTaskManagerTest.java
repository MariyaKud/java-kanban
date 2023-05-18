package service;

import dao.KVServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeFalse;
import static service.HttpTaskManager.loadFromHTTPServer;

@DisplayName("Тест работы менеджера с сервером..")
class HttpTaskManagerTest {
    @DisplayName("Загрузить задачу, эпик, подзадачу с историей. Отсортированные списки должны совпасть.")
    @Test
    void shouldLoadTest() {

        try
        {
            KVServer kvServer = new KVServer();
            kvServer.start();

            HttpTaskManager httpTasksManager = (HttpTaskManager) Managers.getDefault();

            //Подготовить файл
            Managers.getSimpleTestForTaskManager(httpTasksManager);

            try {
                TaskManager loadTasksManager = loadFromHTTPServer(Managers.PORT_KV_SERVER);

                assertEquals(httpTasksManager.getAllTasks(), loadTasksManager.getAllTasks(),
                        "Задачи загрузились не корректны.");
                assertEquals(httpTasksManager.getAllSubTasks(),loadTasksManager.getAllSubTasks(),
                        "Подзадачи загрузились не корректны.");
                assertEquals(httpTasksManager.getAllEpics(),loadTasksManager.getAllEpics(),
                        "Эпики загрузились не корректны.");

                assertEquals(httpTasksManager.getHistory(),loadTasksManager.getHistory(),
                        "История загрузилась не корректно.");
                assertEquals(httpTasksManager.getPrioritizedTasks(),loadTasksManager.getPrioritizedTasks(),
                        "Сортированные списке различаются.");

            } catch (Exception e) {
                System.out.println(Arrays.toString(e.getStackTrace()));
            }

            kvServer.stop();

        } catch (IOException e) {
            System.out.println("Проблемы с подключением к серверу хранилища- " + e.getMessage());
            System.out.println("Тесты не состоялись!");
            assumeFalse(true, "Проблемы с доступом к серверу хранилища");
        }
    }
}