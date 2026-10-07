package mx.com.gs.demogs.backend.util;

import java.time.LocalDateTime;
import java.time.ZoneId;

public final class FechaUtil {

	private static final ZoneId ZONA_MEXICO = ZoneId.of("America/Mexico_City");

	private FechaUtil() {
	}

	public static LocalDateTime ahora() {
		return LocalDateTime.now(ZONA_MEXICO);
	}
}