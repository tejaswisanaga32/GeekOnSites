package com.geekonsite.repository;

import com.geekonsite.model.IVROption;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IVROptionRepository extends MongoRepository<IVROption, String> {
    
    List<IVROption> findByActiveTrueOrderByPriorityAsc();
    
    List<IVROption> findByCategory(IVROption.IssueCategory category);
    
    Optional<IVROption> findByOptionKey(String optionKey);
    
    List<IVROption> findByActiveTrueAndCategoryOrderByPriorityAsc(IVROption.IssueCategory category);
}
