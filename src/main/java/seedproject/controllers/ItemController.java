package seedproject.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import seedproject.domain.Item;
import seedproject.domain.repositories.ItemRepository;

import java.util.List;

@RestController
@RequestMapping("/items")
public class ItemController {
  @Autowired
  private ItemRepository repo;
  
  @RequestMapping(method = RequestMethod.GET)
  public List findItems() {
    return repo.findAll();
  }
  
  @RequestMapping(method = RequestMethod.POST)
  public Item addItem(@RequestBody Item item) {
    item.setId(null);
    return repo.saveAndFlush(item);
  }
  
  @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
  public Item updateItem(@RequestBody Item updatedItem, @PathVariable Integer id) {
    updatedItem.setId(id);
    return repo.saveAndFlush(updatedItem);
  }
  
  @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
  public void deleteItem(@PathVariable Integer id) {
    repo.delete(id);
  }
}
