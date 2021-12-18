package hwnetology.servlet;

import hwnetology.controller.PostController;
import hwnetology.model.Post;
import hwnetology.repository.PostRepository;
import hwnetology.service.PostService;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MainServlet extends HttpServlet {

    private PostController controller;

    // Инициализация начальных данных
    @Override
    public void init() {
        final PostRepository repository = new PostRepository();
        final PostService service = new PostService(repository);
        this.controller = new PostController(service);
        service.save(new Post(0, "Shock, it is working!"));
    }

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