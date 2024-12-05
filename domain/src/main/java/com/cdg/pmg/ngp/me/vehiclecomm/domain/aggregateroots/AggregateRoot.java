package com.cdg.pmg.ngp.me.vehiclecomm.domain.aggregateroots;

import com.cdg.pmg.ngp.me.vehiclecomm.domain.entities.BaseEntity;
import java.io.Serializable;

public abstract class AggregateRoot<T extends Serializable> extends BaseEntity<T> {}
