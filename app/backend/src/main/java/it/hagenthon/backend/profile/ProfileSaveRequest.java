package it.hagenthon.backend.profile;

import java.util.Map;

/** {@code PUT /api/profilo} (sezione 3.5 del contratto). */
public record ProfileSaveRequest(Map<String, String> risposte, Map<String, String> importi) {
}
