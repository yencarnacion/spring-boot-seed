package seedproject.domain.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import seedproject.domain.Item;

public interface ItemRepository extends JpaRepository<Item, Integer> {

}
