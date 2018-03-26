package gorm2.vo


class AccountInfoVO {
    Integer totalAccounts
    Integer totalBalance
    Integer averageBalance

    String toString(){
        "${totalAccounts} : ${totalBalance} : ${averageBalance}"
    }
}
