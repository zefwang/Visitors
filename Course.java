interface IList<T> {
  <R> R callIListVisitor(IListVisitor<T, R> visitor);
}

class MtList<T> implements IList<T> {
  public <R> R callIListVisitor(IListVisitor<T, R> visitor) {
    return visitor.forMt(this);
  }
}

class ConsList<T> implements IList<T> {
  T first;
  IList<T> rest;

  ConsList(T first, IList<T> rest) {
    this.first = first;
    this.rest = rest;
  }

  public <R> R callIListVisitor(IListVisitor<T, R> visitor) {
    return visitor.forCons(this);
  }
}

class Course {
  String name;
  IList<Course> prereqs;

  Course(String name, IList<Course> prereqs) {
    this.name = name;
    this.prereqs = prereqs;
  }

  int getDeepestPathLength() {
    return this.prereqs.callIListVisitor(new DeepestPathLength());
  }

  boolean hasPrereq(String target) {
    return this.prereqs.; // || this.prereqs.callIListVisitor(new HasPrereq());
  }
}

interface IFunc<A, R> {
  R apply(A arg);
}

interface IListVisitor<T, R> extends IFunc<IList<T>, R> {
  R forMt(MtList<T> arg);

  R forCons(ConsList<T> consList);
}

class DeepestPathLength implements IListVisitor<Course, Integer> {
  public Integer apply(IList<Course> arg) {
    return arg.callIListVisitor(this);
  }

  public Integer forMt(MtList<Course> arg) {
    return 0;
  }

  public Integer forCons(ConsList<Course> arg) {
    return Math.max(1 + arg.first.getDeepestPathLength(),
        arg.rest.callIListVisitor(new DeepestPathLength()));
  }
}

interface IPred<X> extends IFunc<X, Boolean>{
  Boolean forMt(MtList<T> arg);
  Boolean forCons(ConsList<T> arg);
}

class HasPrereq implements IPred<Course>{

  public Boolean apply(Course arg) {
    return arg.;  // Temporary
  }

}
