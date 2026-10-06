package mx.com.gs.demogs.backend.core.service;

import mx.com.gs.demogs.backend.core.request.CorreoRequest;

public interface ICorreoService {

	void enviar(CorreoRequest request);
}