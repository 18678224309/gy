package com.jtfu.luban.demo16;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;

public class SenderActor  {

     static class SendActorDemo1 extends AbstractActor {
         @Override
         public Receive createReceive() {
             return receiveBuilder().match(String.class,s -> {
               ActorRef ref=getContext().actorOf(Props.create(SendActorDemo2.class), "actorDemo2");
               ref.tell(s,getSender());
             }).build();
         }
     }

    static class SendActorDemo2 extends AbstractActor {
        @Override
        public Receive createReceive() {
            return receiveBuilder().match(String.class,s -> System.out.println(s+"2")).build();
        }
    }

    public static void main(String[] args) {
        ActorSystem system = ActorSystem.create("sys");
        ActorRef actorRef = system.actorOf(Props.create(SendActorDemo1.class), "actorDemo1");
        actorRef.tell("hello world", ActorRef.noSender());
    }
}
