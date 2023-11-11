package com.gae4coon.cloudmaestro.domain.file;

import com.fasterxml.jackson.annotation.JsonProperty;

public class testDTO {

        @JsonProperty("id")
        private Long id;

        @JsonProperty("name")
        private String name;

        // 기본 생성자
        public testDTO() {}

        // 모든 매개변수를 받는 생성자
        public testDTO(Long id, String name) {
            this.id = id;
            this.name = name;
        }

        // Getter와 Setter
        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        // toString 메소드 (선택적)
        @Override
        public String toString() {
            return "testDTO{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    '}';
        }


}

