package hwnetology.repository;

import hwnetology.exceptions.NotFoundException;
import hwnetology.model.Post;
import org.springframework.stereotype.Repository;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

// Хранилище наших данных
@Repository
public class PostRepository {

    private ConcurrentMap<Long, String> postMap;
    private static AtomicLong idGenerator;
    private final long DELTA_ID = 1;

    public PostRepository() {
        postMap = new ConcurrentHashMap<>();
        idGenerator = new AtomicLong(0);
    }

    private List<Post> convertConcurrentHashMapToList() {
        List<Post> craftedPostList = new LinkedList<>();

        for (Map.Entry entry : postMap.entrySet()) {
            craftedPostList.add(new Post((long)entry.getKey(), entry.getValue().toString()));
        }

        return craftedPostList;
    }

    public List<Post> all() {
        return convertConcurrentHashMapToList();
    }

    public Optional<Post> getById(long id) {
        Post resultPost = new Post();
        List<Post> postList = convertConcurrentHashMapToList();

        for (Post currentPost : postList) {
            if (currentPost.getId() == id) {
                resultPost = currentPost;
                break;
            }
        }
        return Optional.of(resultPost);
    }

    public Post save(Post post) {
        // если id = 0, то создаём новый пост
        Post freshPost = new Post();
        if (post.getId() == 0) {
        freshPost = new Post(idGenerator.addAndGet(DELTA_ID), post.getContent());
            postMap.put(freshPost.getId(), freshPost.getContent());
        }

        // если id !=0, то изменяем имеющийся пост
        if (post.getId() != 0) {
            for (Map.Entry currentPost : postMap.entrySet()) {
                if ((long)currentPost.getKey() == post.getId()) {
                    currentPost.setValue(post.getContent());
                    break;
                }
                else {
                    throw new NotFoundException("There is no post with id = " + post.getId());
                }
            }
        }

        return freshPost;
    }

    public void removeById(long id) {
        for (Map.Entry currentPost : postMap.entrySet()) {
            if((long)currentPost.getKey() == id) {
                postMap.remove(currentPost.getKey());
            }
        }
    }
}