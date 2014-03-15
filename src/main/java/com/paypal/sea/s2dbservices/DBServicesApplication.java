package com.paypal.sea.s2dbservices;

import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;
import javax.ws.rs.ApplicationPath;
@ApplicationPath("/")
public class DBServicesApplication extends Application {
	//line 9
   //private Set<Object> singletons = new HashSet<Object>();
   //private Set<Class<?>> empty = new HashSet<Class<?>>();

//   public DBServicesApplication() {
 //     singletons.add(new Stage2Resource());
  // }

   @Override
   public Set<Class<?>> getClasses() {
       final Set<Class<?>> empty = new HashSet<Class<?>>();
       empty.add(Stage2Resource.class);
      return empty;
   }

 // @Override
 //  public Set<Object> getSingletons() {
  //    return singletons;
   //}
}
