import java.util.Random;

interface Constants
{
    int SEED = 32154;
    int TEST_SIZE = 20000;
    int SMALL_TEST_SIZE = 10;
    int INITIAL_DS_SIZE = TEST_SIZE/4;
    int RANDOM_STRING_LENGTH = 10;
    int VALUES_RANGE = 10000;
    enum Operations {INSERT, DELETE, SEARCH ,RANK, SELECT, SUM_VALUES_IN_INTERVAL}
    enum ALPHABET { A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q,R,S,T,U,V,W,X,Y,Z,a,b,c,d,e,f,g,h,i,j,k,l,m,n,o,
        p,q,r,s,t,u,v,w,x,y,z}
}

class MyKey implements Key
{
    private String str;
    private int identifier;

    @Override
    public String toString()
    {
        return "(" + str + "," + identifier + ")";
    }

    public MyKey(String str, int identifier)
    {
        this.str = str;
        this.identifier = identifier;
    }

    public MyKey(MyKey mykey)
    {
        this.str = mykey.str;
        this.identifier = mykey.identifier;
    }
    @Override
    public MyKey createCopy()
    {
        return new MyKey(this);
    }
    public void setKey(String str, int identifier)
    {
        this.str = str;
        this.identifier = identifier;
    }
    @Override
    public int compareTo(Key rhsKey)
    {
        if (this.str.compareTo(((MyKey) rhsKey).str) < 0)
        {
            return -1;
        }
        if (this.str.compareTo(((MyKey) rhsKey).str) > 0)
        {
            return 1;
        }
        if (this.identifier < ((MyKey) rhsKey).identifier)
        {
            return -1;
        }
        if (this.identifier > ((MyKey) rhsKey).identifier)
        {
            return 1;
        }
        return 0;
    }
}

class MyValue implements Value
{
    private int value;

    public MyValue(int val)
    {
        this.value = val;
    }

    MyValue(MyValue myVal)
    {
        this.value = myVal.value;
    }
    @Override
    public MyValue createCopy()
    {
        return new MyValue(this);
    }
    @Override
    public void addValue(Value valueToAdd)
    {
        this.value = this.value + ((MyValue)valueToAdd).value;
    }
    @Override
    public String toString()
    {
        return String.valueOf(value);
    }
    public void setValue(int value)
    {
        this.value = value;
    }
}

public class Test
{
    private void printRank(MyKey key, int result)
    {
        System.out.println("Rank for key: " + key.toString()+
                " resulted in: " + result);
    }
    private void printSearch(MyKey key, MyValue result)
    {
        System.out.print("Search for key: " + key.toString()+
                " resulted in value: ");
        System.out.println(result);
    }
    private void printSelect(int index, MyKey result)
    {
        System.out.print("Select for index: " + index +
                " resulted in key: ");
        System.out.println(result);
    }
    private void printSumInInterval(MyKey key1, MyKey key2, MyValue result)
    {
        System.out.print("The sum of values in interval [" + key1.toString()
                + "," + key2.toString() + "] is: ");
        System.out.println(result);
    }

    private void executeSimpleTest(BalancedTree T, MyKey key1, MyKey key2, int index)
    {
        printRank(key1,T.rank(key1));
        printSearch(key1,(MyValue) T.search(key1));
        printSelect(index,(MyKey) T.select(index));
        printSumInInterval(key1, key2, (MyValue) T.sumValuesInInterval(key1,key2));
    }

    private void executeSmallTest()
    {
        BalancedTree T = new BalancedTree();

        MyKey[] myKeysArray = new MyKey[Constants.SMALL_TEST_SIZE];
        MyValue[] myValueArray = new MyValue[Constants.SMALL_TEST_SIZE];

        for(int i =0; i<Constants.SMALL_TEST_SIZE;i++)
        {
            myKeysArray[i] = new MyKey("b",i);
            myValueArray[i] = new MyValue(i);
            T.insert(myKeysArray[i],myValueArray[i]);
        }

        for(int i = Constants.SMALL_TEST_SIZE - 1; i >= 0 ;i--)
        {
            T.delete(myKeysArray[i]);
        }

        for(int i = Constants.SMALL_TEST_SIZE - 1; i >= 0 ;i--)
        {
            T.insert(myKeysArray[i],myValueArray[i]);
        }

        for(int i = 0; i<Constants.SMALL_TEST_SIZE;i++)
        {
            T.delete(myKeysArray[i]);
        }
        MyKey key1 = myKeysArray[0];
        MyKey key2 = myKeysArray[1];
        MyValue value = myValueArray[0];
        value.setValue(50);

        executeSimpleTest(T,key1,key2,1);

        T.insert(key1,value);
        executeSimpleTest(T,key1,key2,1);
        key1.setKey("a",2);
        value.setValue(5);
        executeSimpleTest(T,key1,key2,1);
        T.delete(key1);
        executeSimpleTest(T,key1,key2,1);
        key1.setKey("b",1);
        T.delete(key1);
        executeSimpleTest(T,key1,key2,1);
    }

    private String generateRandomString(Random random)
    {
        Constants.ALPHABET[] alphaBet = Constants.ALPHABET.values();
        String str = "";
        for (int i=0;i<Constants.RANDOM_STRING_LENGTH; i++)
        {
            str = str + alphaBet[random.nextInt(alphaBet.length)];
        }
        return str;
    }

    public static void main(String... args)
    {
        Test test = new Test();
        System.out.println("Starting small test...");
        test.executeSmallTest();
        System.out.println("------------------------------------------------------------------");
        System.out.println("Starting test...");

        Constants.Operations[] operationsArray = Constants.Operations.values();
        Random random = new Random();
        random.setSeed(Constants.SEED);

        MyKey[] myKeysArray = new MyKey[Constants.TEST_SIZE+Constants.INITIAL_DS_SIZE];
        MyValue[] myValuesArray = new MyValue[Constants.TEST_SIZE+Constants.INITIAL_DS_SIZE];

        for (int i=0;i<myKeysArray.length;i++)
        {
            myKeysArray[i] = new MyKey(test.generateRandomString(random),i);
            myValuesArray[i] = new MyValue(random.nextInt(Constants.VALUES_RANGE));
        }

        BalancedTree T = new BalancedTree();

        for (int i=0;i<Constants.INITIAL_DS_SIZE;i++)
        {
            T.insert(myKeysArray[i],myValuesArray[i]);
        }

        int currentIndexToInsert = Constants.INITIAL_DS_SIZE;
        int randomIndex;
        MyKey key1;
        MyKey key2;
        Constants.Operations operation;

        key1 = myKeysArray[random.nextInt(myKeysArray.length)];
        key2 = myKeysArray[random.nextInt(myKeysArray.length)];
        if (key1.compareTo(key2) < 0)
        {
            test.printSumInInterval(key2,key1,(MyValue) T.sumValuesInInterval(key2,key1));
        }
        else
        {
            test.printSumInInterval(key1,key2,(MyValue) T.sumValuesInInterval(key1,key2));
        }
        test.printSumInInterval(key1,key1,(MyValue) T.sumValuesInInterval(key1,key1));

        for (int i = 0; i<Constants.TEST_SIZE;i++)
        {
            operation = operationsArray[random.nextInt(operationsArray.length)];
            randomIndex = random.nextInt(currentIndexToInsert);
            key1 = myKeysArray[randomIndex];
            switch (operation)
            {
                case RANK:
                {
                    test.printRank(key1, T.rank(key1));
                    break;
                }
                case DELETE:
                {
                    T.delete(key1);
                    break;
                }
                case INSERT:
                {
                    T.insert(myKeysArray[currentIndexToInsert], myValuesArray[currentIndexToInsert]);
                    currentIndexToInsert += 1;
                    break;
                }
                case SEARCH:
                {
                    test.printSearch(key1, (MyValue) T.search(key1));
                    break;
                }
                case SELECT:
                {
                    test.printSelect(randomIndex, (MyKey) T.select(randomIndex));
                    break;
                }
                case SUM_VALUES_IN_INTERVAL:
                {
                    key1 = myKeysArray[random.nextInt(myKeysArray.length)];
                    key2 = myKeysArray[random.nextInt(myKeysArray.length)];
                    if (key1.compareTo(key2) <= 0)
                    {
                        test.printSumInInterval(key1, key2, (MyValue) T.sumValuesInInterval(key1, key2));
                    }
                    else
                    {
                        test.printSumInInterval(key2, key1, (MyValue) T.sumValuesInInterval(key2, key1));
                    }
                    break;
                }
            }
        }
    }
}
