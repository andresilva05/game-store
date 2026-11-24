package edu.ifsp.games.gamestore.listener;

import edu.ifsp.games.gamestore.config.SessionAttributes;
import edu.ifsp.games.gamestore.model.Usuario;
import jakarta.servlet.ServletContext;
import jakarta.servlet.annotation.WebListener;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpSessionAttributeListener;
import jakarta.servlet.http.HttpSessionBindingEvent;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;

@WebListener
public class SessionListener implements HttpSessionListener, HttpSessionAttributeListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(SessionListener.class);
    private static final AtomicInteger SESSOES_ATIVAS = new AtomicInteger(0);

    @Override
    public void sessionCreated(HttpSessionEvent se) {
        HttpSession session = se.getSession();
        int total = SESSOES_ATIVAS.incrementAndGet();
        atualizarContadorNoContexto(session, total);
        LOGGER.info("Sessão HTTP criada [id={}]. Total de sessões ativas: {}", session.getId(), total);
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        HttpSession session = se.getSession();
        int total = SESSOES_ATIVAS.updateAndGet(atual -> atual > 0 ? atual - 1 : 0);
        atualizarContadorNoContexto(session, total);

        Object attr = null;
        try {
            attr = session.getAttribute(SessionAttributes.USUARIO_LOGADO);
        } catch (IllegalStateException ignored) {
            // Sessão já invalidada pelo container
        }
        if (attr instanceof Usuario usuario) {
            LOGGER.info(
                    "Sessão HTTP encerrada [id={}, usuario='{}']. Total de sessões ativas: {}",
                    session.getId(),
                    usuario.getEmail(),
                    total
            );
        } else {
            LOGGER.info("Sessão HTTP encerrada [id={}]. Total de sessões ativas: {}", session.getId(), total);
        }
    }

    @Override
    public void attributeAdded(HttpSessionBindingEvent event) {
        if (SessionAttributes.USUARIO_LOGADO.equals(event.getName()) && event.getValue() instanceof Usuario usuario) {
            LOGGER.info(
                    "Auditoria de login: usuário '{}' (papel={}) autenticado na sessão [id={}].",
                    usuario.getEmail(),
                    usuario.getPapel(),
                    event.getSession().getId()
            );
        }
    }

    @Override
    public void attributeRemoved(HttpSessionBindingEvent event) {
        if (SessionAttributes.USUARIO_LOGADO.equals(event.getName()) && event.getValue() instanceof Usuario usuario) {
            LOGGER.info(
                    "Auditoria de logout: usuário '{}' removido da sessão [id={}].",
                    usuario.getEmail(),
                    event.getSession().getId()
            );
        }
    }

    public static int getSessoesAtivas() {
        return SESSOES_ATIVAS.get();
    }

    public static void resetarContadorParaTestes() {
        SESSOES_ATIVAS.set(0);
    }

    private void atualizarContadorNoContexto(HttpSession session, int total) {
        ServletContext context = session.getServletContext();
        if (context != null) {
            context.setAttribute(SessionAttributes.SESSOES_ATIVAS, total);
        }
    }
}
