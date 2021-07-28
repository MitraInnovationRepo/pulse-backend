package com.pulse.mst.Entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "ageing")
@Getter
@Setter
public class Ageing {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private String ageName;
    private int status;
}
