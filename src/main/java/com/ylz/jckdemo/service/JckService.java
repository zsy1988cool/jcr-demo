package com.ylz.jckdemo.service;

import org.apache.jackrabbit.core.jndi.RegistryHelper;
import org.apache.jackrabbit.core.jndi.provider.DummyInitialContextFactory;
import org.apache.jackrabbit.value.BinaryImpl;
import org.apache.jackrabbit.value.ValueFactoryImpl;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import org.apache.jackrabbit.core.jndi.RegistryHelper;
import sun.net.www.MimeTable;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import javax.jcr.*;
import javax.jcr.query.*;
import javax.jcr.version.*;

import java.io.*;
import java.util.Calendar;
import java.util.Hashtable;

@Service
public class JckService implements InitializingBean {

    private Session jcrSession;

    @Override
    public void afterPropertiesSet() throws Exception {
        Resource resource = new ClassPathResource("repository.xml");
        File file = resource.getFile();
        String repoFile = file.getAbsolutePath();
        String repoHomeDir = "D:/urule_demo/jck_repo";

        Hashtable<String, Object> env = new Hashtable<>();
        env.put(Context.INITIAL_CONTEXT_FACTORY, DummyInitialContextFactory.class.getName());
        env.put(Context.PROVIDER_URL, "localhost");
        InitialContext ctx = new InitialContext(env);

        RegistryHelper.registerRepository(ctx, "repo", repoFile, repoHomeDir,
                true);
        Repository r = (Repository)ctx.lookup("repo");

        // 登录
        SimpleCredentials cred = new SimpleCredentials("admin", "admin".toCharArray());
        jcrSession = r.login(cred, null);

//        Workspace ws = jcrSession.getWorkspace();
//        ws.getNamespaceRegistry().registerNamespace("wiki",
//                "http://localhost/wiki/1.0");
    }

    public void createNodes() throws RepositoryException{
        // 跟节点
        Node rn = jcrSession.getRootNode();
        // 注册命名空间
        Workspace ws = jcrSession.getWorkspace();

        // 添加内容
        Node encyclopedia = rn.addNode("wiki:encyclopedia");
        Node p = encyclopedia.addNode("wiki:entry");
        p.setProperty("wiki:title", "rose");
        p.setProperty("wiki:content", "A famous poet who like roses.");

        ValueFactory factory = ValueFactoryImpl.getInstance();
        p.setProperty("wiki:category", new Value[] { factory.createValue("flower"),
                factory.createValue("plant"), factory.createValue("rose")});

        Node n = encyclopedia.addNode("wiki:entry");
        n.setProperty("wiki:title", "Shakespeare");
        n.setProperty("wiki:content", "a spears");
        n.setProperty("wiki:category", "poet");

        jcrSession.save();
    }

    public String findData() throws RepositoryException {
        Workspace ws = jcrSession.getWorkspace();
        QueryManager qm = ws.getQueryManager();

        Query q = qm.createQuery("//wiki:encyclopedia/wiki:entry[@wiki:title='rose']",
                Query.XPATH);
        QueryResult result = q.execute();
        NodeIterator iter = result.getNodes();

        StringBuilder builder = new StringBuilder();
        while(iter.hasNext()) {
            Node entry = iter.nextNode();
            Property propNode = entry.getProperty("wiki:content");
            builder.append(propNode.getValue().getString());
        }

        return builder.toString();
    }

    public void saveFile() throws RepositoryException, IOException {
        Resource resource = new ClassPathResource("static/action.xml");
        File file = resource.getFile();
        String resFile = file.getAbsolutePath();

        MimeTable mt = MimeTable.getDefaultTable();
        String mimeType = mt.getContentTypeFor(file.getName());
        if(mimeType == null) {
            mimeType = "application/octet-stream";
        }

        Node rn = jcrSession.getRootNode();
        String filenName = file.getName();
        Node fileNode = rn.addNode(filenName, "nt:file");
        Node resNode = fileNode.addNode("jcr:content", "nt:resource");
        resNode.setProperty("jcr:mimeType", mimeType);
        resNode.setProperty("jcr:encoding", "utf-8");

        // 用流加入
        FileInputStream fileInputStream = new FileInputStream(file);
        Binary fileBinary = new BinaryImpl(fileInputStream);
        resNode.setProperty("jcr:data", fileBinary);

        Calendar lastModified = Calendar.getInstance();
        lastModified.setTimeInMillis(file.lastModified());
        resNode.setProperty("jcr:lastModified", lastModified);

        jcrSession.save();

        fileInputStream.close();
    }

    public void readFileFromContent() throws RepositoryException {
        Workspace ws = jcrSession.getWorkspace();
        QueryManager qm = ws.getQueryManager();

        Query q = qm.createQuery("//action.xml/jcr:content",
                Query.XPATH);
        QueryResult result = q.execute();
        NodeIterator iter = result.getNodes();

        while(iter.hasNext()) {
            FileOutputStream fileOutputStream = null;
            InputStream inputStream = null;
            try {
                Node entry = iter.nextNode();
                Property propNode = entry.getProperty("jcr:data");
                Binary value = propNode.getValue().getBinary();

                fileOutputStream = new FileOutputStream("D:\\action1.xml");
                inputStream = value.getStream();

                int size = (int)value.getSize();
                byte[] bytes = new byte[size + 1];
                if(-1 == inputStream.read(bytes)) {
                    break;
                }

                fileOutputStream.write(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if(inputStream != null)
                        inputStream.close();
                    if(fileOutputStream != null)
                        fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }
}
