package com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Attachment {

  private String filename;
  private String url;
  private String type;
  private String s3Bucket;
  private String s3Key;
}
