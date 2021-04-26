package com.pulse.mst.Entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "features")
@Getter
@Setter
public class Features {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private String featureName;
    private int roleId;
}
