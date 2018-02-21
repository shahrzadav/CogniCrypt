package de.cognicrypt.staticanalyzer.telemetry;

/**
 * Possible telemetry events.
 *
 */
public enum TelemetryEvents {
	START, STOP, POST_BUILD, ANALYSIS_INTERNAL_ERROR, ANALYSIS_FINISHED, ANALYSIS_ABORTED, ANALYSIS_ERROR_SETUP, SOOT_EXCEPTION
}
