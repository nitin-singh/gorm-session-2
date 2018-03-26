package gormdemo2

class Branch {

    String name
    String address

    static hasMany = [accounts: Account]

}
