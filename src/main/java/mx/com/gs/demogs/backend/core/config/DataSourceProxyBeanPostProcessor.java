package mx.com.gs.demogs.backend.core.config;

import javax.sql.DataSource;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

import net.ttddyy.dsproxy.support.ProxyDataSource;
import net.ttddyy.dsproxy.support.ProxyDataSourceBuilder;

@Slf4j
@Component
public class DataSourceProxyBeanPostProcessor implements BeanPostProcessor {

	@Value("${coregs.database.slow-query-threshold-ms:500}")
	private long tiempoMinimoConsultaMs;

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {

		if (bean instanceof DataSource dataSource && !(bean instanceof ProxyDataSource)) {

			return ProxyDataSourceBuilder.create(dataSource).name("CoreGS-BD").afterQuery((execInfo, queryInfoList) -> {

				long tiempo = execInfo.getElapsedTime();

				if (tiempo >= tiempoMinimoConsultaMs) {

					queryInfoList.forEach(queryInfo -> {

						String sql = queryInfo.getQuery().replaceAll("\\s+", " ").trim();

						log.warn("CONSULTA LENTA | SQL: {} | TIEMPO: {} ms", sql, tiempo);
					});
				}
			}).build();
		}

		return bean;
	}
}