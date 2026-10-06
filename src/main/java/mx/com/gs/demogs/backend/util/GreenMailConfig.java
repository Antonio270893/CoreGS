package mx.com.gs.demogs.backend.util;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetup;

@Configuration
public class GreenMailConfig {

	@Bean(initMethod = "start", destroyMethod = "stop")
	public GreenMail greenMail() {

		ServerSetup smtp = new ServerSetup(1025, "localhost", ServerSetup.PROTOCOL_SMTP);

		return new GreenMail(smtp);
	}
}