package hwnetology.servlet;

import hwnetology.controller.PostController;
import hwnetology.model.Post;
import hwnetology.service.PostService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MainServlet extends HttpServlet {

    final AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext("hwnetology");
    final PostController controller = context.getBean(PostController.class);
    final Post testPost = context.getBean(PostService.class).save(new Post(0, "Shock, it is working!"));


    // Метод, выполняющийся при каждом запросе
    public void service(HttpServletRequest request, HttpServletResponse response) {

        try {
            final String path = request.getRequestURI();
            final String method = request.getMethod();

            // Простая реализация роутинга
            // Вернуть все посты
            if (method.equals("GET") && path.equals("/api/posts")) {
                controller.all(response);
                return;
            }

            // Вернуть один пост по его id
            if (method.equals("GET") && path.matches("/api/posts/\\d+")) {
                controller.getById(parseId(path), response);
                return;
            }

            // Если прилетел метод POST
            if (method.equals("POST") && path.equals("/api/posts")) {
                controller.save(request.getReader(), response);
                return;
            }

            // Если прилетел метод DELETE
            if (method.equals("DELETE") && path.matches("/api/posts/\\d+")) {
                controller.removeById(parseId(path), response);
                return;
            }
            // Если совпадений не найдено, то выбрасываем ошибку
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
        catch (Exception exception) {
            exception.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private long parseId(String path) {
        return Long.parseLong(path.substring(path.lastIndexOf("/") + 1));
    }
}