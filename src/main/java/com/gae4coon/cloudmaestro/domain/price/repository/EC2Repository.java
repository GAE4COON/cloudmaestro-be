package com.gae4coon.cloudmaestro.domain.price.repository;

import com.gae4coon.cloudmaestro.domain.price.entity.EC2Entity;
import com.gae4coon.cloudmaestro.domain.price.entity.MemberEntity;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;



@Repository
public interface EC2Repository extends JpaRepository<EC2Entity,Integer> {

}
