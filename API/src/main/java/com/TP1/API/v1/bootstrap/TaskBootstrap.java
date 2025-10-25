package com.TP1.API.v1.bootstrap;

import com.TP1.API.v1.modules.task.model.Task;
import com.TP1.API.v1.modules.task.repository.TaskRepository;
import com.opencsv.bean.CsvToBeanBuilder;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

@Slf4j
@AllArgsConstructor
@Component
@Profile({"docker-compose","dev","prod"})
public class TaskBootstrap implements CommandLineRunner {

    private final TaskRepository taskRepository;

    @Override
    public void run(String... args) throws Exception {
        log.info("Running TaskBootstrap");

        loadData();

        log.info("TaskBootstrap completed successfully.");
    }

    public void loadData() {
        try {
            // Usar ClassPathResource en lugar de ResourceUtils
            Reader reader = new InputStreamReader(
                    new ClassPathResource("data/tasks_data.csv").getInputStream()
            );


            List<TaskRecordDTO> taskRecordDTOS = convertCSV(reader);

            if (taskRepository.count() < 20) {
                log.info("Loading database with tasks");
                for (TaskRecordDTO taskRecordDTO : taskRecordDTOS) {
                    taskRepository.save(
                            Task.builder()
                                    .title(taskRecordDTO.getTitle())
                                    .description(taskRecordDTO.getDescription())
                                    .completed(taskRecordDTO.isCompleted())
                                    .build()
                    );
                    log.info("Task '{}' saved.", taskRecordDTO.getTitle());
                }
            } else {
                log.info("Task repository already contains sufficient data.");
            }

        } catch (Exception e) {
            log.error("Error loading tasks_data.csv", e);
        }
    }

    public List<TaskRecordDTO> convertCSV(Reader reader) {
        List<TaskRecordDTO> taskRecordDTOS =
                new CsvToBeanBuilder<TaskRecordDTO>(reader)
                        .withType(TaskRecordDTO.class)
                        .build()
                        .parse();
        log.info("CSV parsed into TaskRecordDTO list");
        return taskRecordDTOS;
    }
}
