package org.codehaus.mojo.jaxb2.shared;

import org.apache.maven.plugin.logging.Log;

import java.io.UnsupportedEncodingException;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;

/**
 * Handler implementation which delegates its actual logging to an internal Maven log.
 *
 * @author <a href="mailto:lj@jguru.se">Lennart J&ouml;relid</a>, jGuru Europe AB
 */
public class MavenLogHandler extends Handler {

    // Internal state
    private Log log;
    private String prefix;

    /**
     * Creates a new MavenLogHandler which adapts a Handler to emit log messages onto a Maven Log.
     *
     * @param log      The Maven Log to emit log messages to.
     * @param prefix   An optional prefix used to prefix any log message.
     * @param encoding The encoding which should be used.
     */
    public MavenLogHandler(final Log log,
                           final String prefix,
                           final String encoding) {

        // Check sanity
        Validate.notNull(log, "log");
        Validate.notNull(prefix, "prefix");
        Validate.notEmpty(encoding, "encoding");

        // Assign internal state
        this.log = log;
        this.prefix = prefix.isEmpty() ? "" : "[" + prefix + "]: ";

        setFormatter(new SimpleFormatter());
        setLevel(getJavaUtilLoggingLevelFor(log));
        try {
            setEncoding(encoding);
        } catch (UnsupportedEncodingException e) {
            log.error("Could not use encoding '" + encoding + "'", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void publish(final LogRecord record) {

        if (this.isLoggable(record)) {

            final Level level = record.getLevel();
            final String message = prefix + getFormatter().format(record);

            if (Level.SEVERE.equals(level)) {
                log.error(message);
            } else if (Level.WARNING.equals(level)) {
                log.warn(message);
            } else if (Level.INFO.equals(level)) {
                log.info(message);
            } else {
                log.debug(message);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void flush() {
        // Do nothing.
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() throws SecurityException {
        // Do nothing.
    }

    /**
     * Retrieves the JUL Level matching the supplied Maven Log.
     *
     * @param mavenLog A non-null Maven Log.
     * @return The Corresponding JUL Level.
     */
    public static Level getJavaUtilLoggingLevelFor(final Log mavenLog) {

        // Check sanity
        Validate.notNull(mavenLog, "mavenLog");

        Level toReturn = Level.SEVERE;

        if (mavenLog.isDebugEnabled()) {
            toReturn = Level.FINER;
        } else if (mavenLog.isInfoEnabled()) {
            toReturn = Level.INFO;
        } else if (mavenLog.isWarnEnabled()) {
            toReturn = Level.WARNING;
        }

        // All Done.
        return toReturn;
    }
}
