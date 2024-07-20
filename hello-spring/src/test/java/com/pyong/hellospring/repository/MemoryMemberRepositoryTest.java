package com.pyong.hellospring.repository;

import com.pyong.hellospring.domain.Member;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.List;

public class MemoryMemberRepositoryTest {
    MemoryMemberRepository repository = new MemoryMemberRepository();
    @AfterEach
    public void afterEach() {
        repository.clearStore();
    }

    @Test
    public void save() {
        Member member = new Member();
        member.setName("spring");

        repository.save(member);

        Member result = repository.findById(member.getId()).get();
        Assertions.assertThat(member).isEqualTo(result);
    }

    @Test
    public void findByName() {
        Member member01 = new Member();
        member01.setName("spring01");
        repository.save(member01);

        Member member02 = new Member();
        member02.setName("spring02");
        repository.save(member02);

        Member result = repository.findByName("spring01").get();

        Assertions.assertThat(result).isEqualTo(member01);
    }

    @Test
    public void findAll() {
        Member member01 = new Member();
        member01.setName("spring01");
        repository.save(member01);

        Member member02 = new Member();
        member02.setName("spring02");
        repository.save(member02);

        List<Member> result = repository.findAll();

        Assertions.assertThat(result.size()).isEqualTo(2);

    }
}
