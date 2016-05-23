import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import ch.qos.logback.core.ConsoleAppender
import ch.qos.logback.core.rolling.RollingFileAppender
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy

def logDir = System.properties["catalina.home"] + "/logs/entelect/"
def logPattern = "%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n"

appender("CONSOLE", ConsoleAppender) {
    encoder(PatternLayoutEncoder) {
        pattern = logPattern
    }
}

appender("FILE", RollingFileAppender) {
    file = logDir + "entelect-web.log"
    encoder(PatternLayoutEncoder) {
        pattern = logPattern
    }
    rollingPolicy(TimeBasedRollingPolicy) {
        fileNamePattern = logDir + "entelect-web.%d{yyyy-MM-dd}.log"
        maxHistory = 30
    }
}

logger("za.co.entelect", DEBUG)

//logger("com.zaxxer.hikari", DEBUG);

//logger("org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping", INFO)

//logger("org.springframework.security", DEBUG)

root(WARN, ["CONSOLE", "FILE"])
