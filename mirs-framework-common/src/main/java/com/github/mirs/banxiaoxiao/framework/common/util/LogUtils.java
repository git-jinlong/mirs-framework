package com.github.mirs.banxiaoxiao.framework.common.util;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;

/**
 * @author bc
 */
@Slf4j
public class LogUtils {


    /**
     * 删除某个路径特定天数前的文件,谨慎使用，只用于特定路径，其他请不用乱删，防止删除系统文件
     *
     * @param filePath 文件路径
     * @param day      天数
     */
    public static void deleteLogFile(String filePath, int day) {

        if (StringUtil.isEmpty(filePath)) {
            return;
        }

        Path path = Paths.get(filePath);

        boolean isExists = Files.exists(path);
        if (!isExists) {
            log.error("deleteLogFile check  filePath is not exists");
            return;
        }

        try {
            Files.list(path).forEach(tempPath -> {
                File file = tempPath.toFile();
                long time = file.lastModified();
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(time);
                Instant instant = calendar.getTime().toInstant();
                ZoneId zoneId = ZoneId.systemDefault();
                LocalDate fileDate = instant.atZone(zoneId).toLocalDate();
                LocalDate nowDate = LocalDate.now();
                long date = nowDate.toEpochDay() - fileDate.toEpochDay();

                if (date >= day) {
                    file.delete();
                }
            });
        } catch (IOException ex) {
            log.error("deleteLogFile filePath={} error", filePath);
        }
    }

}
