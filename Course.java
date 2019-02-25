import tester.Tester;

interface IList<T> {
  <R> R accept(IListVisitor<T, R> visitor);
}

class MtList<T> implements IList<T> {
  public <R> R accept(IListVisitor<T, R> visitor) {
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

  public <R> R accept(IListVisitor<T, R> visitor) {
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
    return this.prereqs.accept(new DeepestPathLength());
  }

  boolean hasPrereq(String target) {
    return new HasPrereq(target).apply(this);
    // .hasClass(target);
  }

  boolean hasName(String target) {
    return this.name.equals(target) || this.hasPrereq(target);
    // The current prereq's name and it's prereqs
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
    return arg.accept(this);
  }

  public Integer forMt(MtList<Course> arg) {
    return 0;
  }

  public Integer forCons(ConsList<Course> arg) {
    return Math.max(1 + arg.first.getDeepestPathLength(), arg.rest.accept(new DeepestPathLength()));
  }
}

interface IPred<X> extends IFunc<X, Boolean> {
}

class HasPrereq implements IPred<Course> {
  String target;

  HasPrereq(String target) {
    this.target = target;
  }

  public Boolean apply(Course arg) {
    return new HasReqHelper(target).apply(arg.prereqs);
  }
}

class HasReqHelper implements IListVisitor<Course, Boolean> {
  String target;

  HasReqHelper(String target) {
    this.target = target;
  }

  public Boolean apply(IList<Course> arg) {
    return arg.accept(this);
  }

  public Boolean forMt(MtList<Course> arg) {
    return false;
  }

  public Boolean forCons(ConsList<Course> arg) {
    return arg.first.hasName(target) || arg.rest.accept(this);
  }

}

class ExamplesCourses {
  Course c1 = new Course("CS 1", new MtList<Course>());
  Course c2 = new Course("CS 2", new MtList<Course>());
  IList<Course> cList1And2 = new ConsList<Course>(c2,
      new ConsList<Course>(c1, new MtList<Course>()));
  Course c3 = new Course("CS 3", cList1And2);
  Course c4 = new Course("CS 4", new MtList<Course>());
  Course c5 = new Course("CS 5", new MtList<Course>());
  IList<Course> cList34And5 = new ConsList<Course>(c3,
      new ConsList<Course>(c4, new ConsList<Course>(c5, new MtList<Course>())));
  Course c6 = new Course("CS 6", cList34And5);
  Course c7 = new Course("CS 7", new ConsList<Course>(c6, new MtList<Course>()));

  boolean testDeepestPathLength(Tester t) {
    return t.checkExpect(c1.getDeepestPathLength(), 0)
        && t.checkExpect(c3.getDeepestPathLength(), 1)
        && t.checkExpect(c6.getDeepestPathLength(), 2)
        && t.checkExpect(c4.getDeepestPathLength(), 0)
        && t.checkExpect(c7.getDeepestPathLength(), 3);
  }

  boolean testHasPrereq(Tester t) {
    return t.checkExpect(c1.hasPrereq("CS 3"), false) && t.checkExpect(c3.hasPrereq("CS 1"), true)
        && t.checkExpect(c3.hasPrereq("CS 4"), false) && t.checkExpect(c4.hasPrereq("CS 1"), false)
        && t.checkExpect(c6.hasPrereq("CS 1"), true) && t.checkExpect(c7.hasPrereq("CS 5"), true)
        && t.checkExpect(c7.hasPrereq("CS 2"), true) && t.checkExpect(c7.hasPrereq("CS 0"), false);
  }
}
