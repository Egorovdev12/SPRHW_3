package hwnetology.controller;

import com.google.gson.Gson;
import hwnetology.model.Post;
import hwnetology.service.PostService;
import org.springframework.stereotype.Controller;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Reader;

@Controller
public class PostController {

    public static final String APPLICATION_JSON = "application/json";
    private final PostService service;

    public PostController (PostService service) {
     this.service = service;
    }

    public void all(HttpServletResponse response) throws IOException {
        // устанавливаем тип ответа
        response.setContentType(APPLICATION_JSON);
        // получаем список постов
        final var data = service.all();
        // возвращаем клиенту список постов в json-формате
        final var gson = new Gson();
        response.getWriter().print(gson.toJson(data));
    }

    public void getById(long id, HttpServletResponse response) throws IOException {
        response.setContentType(APPLICATION_JSON);
        // получаем нужный пост
        final Post data = service.getById(id);
        // Возвращаем пост клиенту в JSON-формате
        final var gson = new Gson();
        response.getWriter().print(gson.toJson(data));
    }

    public void removeById(long id, HttpServletResponse response) throws IOException {
        service.removeById(id);
        response.getWriter().print("An attempt was made to DELETE a post with id = " + id);
    }

    public void save(Reader body, HttpServletResponse response) throws IOException {
        response.setContentType(APPLICATION_JSON);
        final Gson gson = new Gson();
        final Post post = gson.fromJson(body, Post.class);
        final Post data = service.save(post);
        response.getWriter().print(gson.toJson(data));
    }
}