package prog_mobile.uqac.com.scanmonsters.database;

public class Notification {


    public static final int FRIEND_REQUEST = 0;
    public static final int MESSAGE_REQUEST = 1;
    public static final int CREATURE_EXCHANGE_REQUEST = 2;

    public int id;
    public int idAccount;
    public String login1;
    public int idRecepteur;
    public String login2;
    public int state;
    public int idType;
    public String data;
    public long dateCreation;
    public long dateLastUpdate;
    public boolean imHost;

    public Notification(){}

    public void fromRawData(String rawData) {

        //$data["id"]."-".$data["idAccount"]."-".$data["login1"]."-".$data["idRecepteur"]."-".$data["login2"]."-".$data["state"]."-".$data["idType"]."-".$data["data"]."-".$data["dateCreation"]."-".$data["dateLastUpdate"];
        String[] notificationData = rawData.split("-");
        id = Integer.parseInt(notificationData[0]);
        idAccount = Integer.parseInt(notificationData[1]);
        login1 = notificationData[2];
        idRecepteur = Integer.parseInt(notificationData[3]);
        login2 = notificationData[4];
        state = Integer.parseInt(notificationData[5]);
        idType = Integer.parseInt(notificationData[6]);
        data = notificationData[7];
        dateCreation = Long.parseLong(notificationData[8]);
        dateLastUpdate = Long.parseLong(notificationData[9]);
        imHost = (notificationData[10].equals("1"))? true:false;
    }

    public String getRawData() {
        return id+"-"+idAccount+"-"+login1+"-"+idRecepteur+"-"+login2+"-"+state+"-"+idType+"-"+data+"-"+dateCreation+"-"+dateLastUpdate+"-"+(imHost?1:0);
    }


    public String getLoginOther(String myLogin) {

        if(myLogin.equalsIgnoreCase(login1)) return login2;
        return login1;
    }
    public boolean isReceived() { // Si la notification est pour moi ou si je l'ai envoyée
        //TODO : Vérifier tous les cas

        if(imHost && state > 0) return true;
        if(!imHost && (state == 0 || state == 1)) return true;
        return false;
    }

    public String getDisplayableNotif() {
        // State : 0 envoyé, 1 ouvert, 2 accepté, 3 refusé, 4 archivé
        // Type  : 0 demande ami, 1 message, 2 échange de créature
        String result = "";
        switch(idType)
        {
            case 0:
                if(imHost)
                {
                    if(state == 0 || state == 1)
                        result = "Demande d'amitié à "+login2+" en cours";
                    else if(state == 2)
                        result = login2+" a accepté votre demande";
                    else if(state == 3)
                        result = login2+" a refusé votre demande";
                }
                else
                {
                    if(state == 0 || state == 1)
                        result = login1+" vous demande en ami";
                    else if(state == 2)
                        result = "Acceptation de "+login1+" en ami";
                    else if(state == 3)
                        result = "Refus de "+login1+" en ami";
                }
                break;
            case 1:
                result = "Message entre "+login1+" et "+login2;
                break;
            case 2:
                if(imHost)
                {
                    if(state == 0 || state == 1)
                        result = "Cadeau envoyé à "+login2+" (Créature n°"+data+")";
                    else if(state == 2 || state == 3)
                        result = login2+" a bien reçu votre cadeau.";
                }
                else
                {
                    if(state == 0 || state == 1)
                        result = "Cadeau de "+login1+" (Créature n°"+data+")";
                    else if(state == 2 || state == 3)
                        result = "Acceptation du cadeau de "+login1+" (Créature n°"+data+")";
                }

                break;
        }
        return result;
    }

    public int getIntData() {
        try{
            return Integer.parseInt(data);
        }catch(Exception e)
        {
            return 0;
        }
    }

    public String getDisplayableType() {
        // State : 0 envoyé, 1 ouvert, 2 accepté, 3 refusé, 4 archivé
        // Type  : 0 demande ami, 1 message, 2 échange de créature
        String result = "";
        switch(idType)
        {
            case 0:
                result = "Demande d'amitié";
                break;
            case 1:
                result = "Message";
                break;
            case 2:
                result = "Echange de créature";
                break;
        }
        return result;
    }

    public int compare(Notification left, Notification right) {
        return (left.dateLastUpdate < right.dateLastUpdate)?-1:1;
    }

    @Override
    public String toString() {
        return "Notification";
    }
}