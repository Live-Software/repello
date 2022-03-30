package org.live.lobbyserver.util;

import java.time.Instant;
import org.springframework.stereotype.Component;

@Component
public class DateUtil {
    public Instant getCurrentTime() {
        return Instant.now();
    }
}
