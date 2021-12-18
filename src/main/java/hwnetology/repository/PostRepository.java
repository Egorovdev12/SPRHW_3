package hwnetology.repository;

import hwnetology.exceptions.NotFoundException;
import hwnetology.model.Post;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

// Хранилище наших данных
public class PostRepository {

    private List<Post> postList;
    private static AtomicLong idGenerator;
    private final long DELTA_ID = 1;

    public PostRepository() {
        postList = new LinkedList<>();
        idGenerator = new AtomicLong(0);
    }

    public synchronized List<Post> all() {
        return postList;
    }

    public synchronized Optional<Post> getById(long id) {
        Post resultPost = new Post();

        for (Post currentPost : postList) {
            if (currentPost.getId() == id) {
                resultPost = currentPost;
                break;
            }
        }
        return Optional.of(resultPost);
    }

    public synchronized Post save(Post post) {
        // если id = 0, то создаём новый пост
        Post freshPost = new Post();
        if (post.getId() == 0) {
        freshPost = new Post(idGenerator.addAndGet(DELTA_ID), post.getContent());
            postList.add(freshPost);
        }

        // если id !=0, то изменяем имеющийся пост
        if (post.getId() != 0) {
            for (Post currentPost : postList) {
                if (currentPost.getId() == post.getId()) {
                    currentPost.setContent(post.getContent());
                    break;
                }
                else {
                    throw new NotFoundException("There is no post with id = " + post.getId());
                }
            }
        }

        return freshPost;
    }

    public synchronized void removeById(long id) {
        for (Post currentPost : postList) {
            if(currentPost.getId() == id) {
                postList.remove(currentPost);
            }
        }
    }
}