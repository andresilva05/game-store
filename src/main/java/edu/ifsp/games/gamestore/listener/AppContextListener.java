package edu.ifsp.games.gamestore.listener;

import edu.ifsp.games.gamestore.config.ConnectionFactory;
import edu.ifsp.games.gamestore.config.SessionAttributes;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebListener
public class AppContextListener implements ServletContextListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(AppContextListener.class);

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        sce.getServletContext().setAttribute(SessionAttributes.SESSOES_ATIVAS, 0);
        LOGGER.info("GameStore inicializada com sucesso.");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        ConnectionFactory.closePool();
        LOGGER.info("Pool de conexões HikariCP encerrado. GameStore finalizada.");
    }
}
